package com.kalhan.security_service.jwt;

import com.kalhan.security_service.entity.User;
import com.kalhan.security_service.repository.UserRepository;
import com.kalhan.security_service.service.UserApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserApiClient userApiClient;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userApiClient.findUserByEmail(username);
        if (user == null) {
            throw new BadCredentialsException("email and / or Password is incorrect");
        }
        return user;
    }
}