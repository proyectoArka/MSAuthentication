package com.arka.MSAuthentication.infrastructure.adapters.repository;

import com.arka.MSAuthentication.infrastructure.adapters.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByNameRole(String nameRole);
}
