package com.kalhan.security_service.repository;


import com.kalhan.security_service.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String passwordResetToken);
}
