package com.kalhan.security_service.config;

import com.kalhan.security_service.entity.Role;
import com.kalhan.security_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
@RequiredArgsConstructor
public class RoleConfig {

    private final RoleRepository roleRepository;
    private final MongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner initializeRoles() {
        return args -> {
            // Ensure 'name' field is indexed uniquely
            IndexOperations indexOps = mongoTemplate.indexOps(Role.class);

            addRoleIfNotExists("USER");
            addRoleIfNotExists("ADMIN");
        };
    }

    private void addRoleIfNotExists(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
        }
    }
}

