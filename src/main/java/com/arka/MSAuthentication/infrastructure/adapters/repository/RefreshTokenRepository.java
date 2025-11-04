package com.arka.MSAuthentication.infrastructure.adapters.repository;

import com.arka.MSAuthentication.infrastructure.adapters.entity.RefreshTokenEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUserEntity(UserEntity userEntity);
}