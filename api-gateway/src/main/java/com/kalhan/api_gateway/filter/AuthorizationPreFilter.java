package com.kalhan.api_gateway.filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalhan.api_gateway.dto.ExceptionResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AuthorizationPreFilter extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    private static final List<String> adminListEndpoints = List.of(
            "/user/block-user"
    );

    private final ObjectMapper objectMapper;

    public AuthorizationPreFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (adminListEndpoints.stream().anyMatch(request.getURI().getPath()::startsWith)) {
                if (!isAdmin(exchange)) {
                    return onError(exchange, "403", "Forbidden", "User is not authorized to access this resource", HttpStatus.FORBIDDEN);
                }
            }

            return chain.filter(exchange);
        };
    }

    private boolean isAdmin(ServerWebExchange exchange) {
        List<String> authorities = exchange.getRequest().getHeaders().get("authorities");
        return authorities != null && authorities.contains("ROLE_ADMIN");
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
}

