package com.kalhan.security_service.repository;

import com.kalhan.security_service.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, Integer> {
    Optional<Role> findByName(String roleStudent);
}
