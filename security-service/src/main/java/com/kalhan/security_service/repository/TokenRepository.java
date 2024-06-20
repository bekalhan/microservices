package com.kalhan.security_service.repository;

import com.kalhan.security_service.entity.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, Integer> {

    @Query("{ 'user.id' : ?0, 'expired' : false, 'revoked' : false }")
    List<Token> findByValidTokenByUser(String userId);

    Optional<Token> findByToken(String token);
}