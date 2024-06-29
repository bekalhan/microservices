package com.kalhan.api_gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalhan.api_gateway.dto.ExceptionResponseModel;
import com.kalhan.api_gateway.dto.ValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticationPreFilter extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    public static final List<String> whiteListEndpoints = List.of(
            "/auth/register",
            "/auth/authenticate",
            "/auth/activate-token",
            "/auth/refresh-token",
            "/auth/password-reset-request",
            "/auth/reset-password",
            "/user/get-followers",
            "/user/get-following",
            "/post/get-all-posts",
            "/post/get-post/",
            "/post/get-user-posts/",
            "/post/get-post-likes/",
            "/post/get-post-saved-count/"
            // Add other endpoints that require authentication here
    );


    private static final List<String> adminListEndpoints = List.of(
            "/user/block-user"
    );

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public AuthenticationPreFilter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    private String getBearerToken(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> authorizationHeaders = headers.get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authHeader = authorizationHeaders.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            } else {
                throw new RuntimeException("Invalid authorization header format");
            }
        } else {
            throw new RuntimeException("Authorization header missing");
        }
    }

    private Predicate<ServerHttpRequest> isAdminSecured =
            request -> adminListEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

    private Predicate<ServerHttpRequest> isSecured =
            request -> whiteListEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            if (isSecured.test(exchange.getRequest())) {
                String bearerToken = getBearerToken(exchange);
                return webClientBuilder.build().get()
                        .uri("lb://security-service/auth/validateToken")
                        .header("Authorization", bearerToken)
                        .retrieve()
                        .bodyToMono(ValidationResponse.class)
                        .flatMap(response -> {
                            ServerHttpRequest request = exchange.getRequest().mutate()
                                    .header("username", response.getUsername())
                                    .header("id",response.getId())
                                    .header("authorities", String.join(",", response.getAuthorities()))
                                    .header("auth-token", response.getToken())
                                    .build();

                            // Check if the path requires admin role
                            if (adminListEndpoints.stream().anyMatch(exchange.getRequest().getURI().getPath()::startsWith)) {
                                // Check if the user has admin role
                                if (!isAdmin(exchange)) {
                                    return onError(exchange, "403", "Forbidden", "User is not authorized to access this resource", HttpStatus.FORBIDDEN);
                                }
                            }

                            return chain.filter(exchange.mutate().request(request).build());
                        })
                        .onErrorResume(error -> {
                            log.error("Error during authentication: {}", error.getMessage());
                            return onError(exchange, "500", "Internal Server Error", "Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR);
                        });
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, String errDetails, HttpStatus httpStatus) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        try {
            response.getHeaders().add("Content-Type", "application/json");
            ExceptionResponseModel data = new ExceptionResponseModel(errCode, err, errDetails, null, new Date());
            byte[] byteData = objectMapper.writeValueAsBytes(data);
            return response.writeWith(Mono.just(byteData).map(dataBufferFactory::wrap));
        } catch (Exception e) {
            log.error("Error handling exception: {}", e.getMessage());
            return response.setComplete();
        }
    }

    private boolean isAdmin(ServerWebExchange exchange) {
        List<String> authorities = exchange.getRequest().getHeaders().get("authorities");
        return authorities != null && authorities.contains("ROLE_ADMIN");
    }
}
