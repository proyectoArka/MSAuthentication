package com.arka.MSAuthentication.infrastructure.config.RefreshToken;


import com.arka.MSAuthentication.infrastructure.adapters.entity.RefreshTokenEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import com.arka.MSAuthentication.infrastructure.adapters.repository.RefreshTokenRepository;
import com.arka.MSAuthentication.infrastructure.adapters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-toke.expiration-ms:604800000}")
    private Long refreshTokenDurationMs;


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository =  userRepository;
    }

    public RefreshTokenEntity createRefreshToken(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.arka.MSAuthentication.domain.model.Exception.ResourceNotFoundException("Usuario con email: " + email + " no encontrado"));

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
            refreshToken.setUserEntity(userEntity);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isRefreshTokenValidExpired(RefreshTokenEntity token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
    @Transactional
    public void deleteRefreshToken(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.arka.MSAuthentication.domain.model.Exception.ResourceNotFoundException("Usuario con email: " + email + " no encontrado"));
        refreshTokenRepository.deleteByUserEntity(userEntity);
    }
}
