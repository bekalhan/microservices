package com.kalhan.security_service.repository;

import com.kalhan.security_service.entity.ActivationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends MongoRepository<ActivationToken, String> {
    Optional<ActivationToken> findByToken(String token);
    void deleteByToken(String token);
}
