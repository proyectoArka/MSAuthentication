package com.arka.MSAuthentication.infrastructure.adapters.repository;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    UserEntity save(UserEntity userEntity);
    //Optional<UserEntity> findByEmail(String username);


}
