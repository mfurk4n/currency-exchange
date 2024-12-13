package com.finexchange.finexchange.service;

import com.finexchange.finexchange.model.RefreshToken;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${refresh.token.expires.in}")
    private Long expireSeconds;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(User user) {
        RefreshToken token = getByUser(user.getId());
        if (token == null) {
            token = new RefreshToken();
            token.setUser(user);
        }
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Date.from(Instant.now().plusSeconds(expireSeconds)));
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    public boolean isRefreshExpired(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }

    public RefreshToken getByUser(String userId) {
        return refreshTokenRepository.findByUserId(userId).orElse(null);
    }
}
