package com.arka.MSAuthentication.infrastructure.adapters.repository;

import com.arka.MSAuthentication.domain.model.User;
import com.arka.MSAuthentication.domain.model.gateway.UserGateway;
import com.arka.MSAuthentication.infrastructure.adapters.entity.RoleEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserGatewayImpl implements UserGateway {

    private final UserRepository UserRepository;
    private final RoleRepository roleRepository;

    public UserGatewayImpl(UserRepository UserRepository, RoleRepository roleRepository) {
        this.UserRepository = UserRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User SaveUser(User user) {
        // 1. Buscar el rol y manejar el Optional
        RoleEntity roleEntity = roleRepository.findByNameRole(user.getRole())
                .orElseThrow(() -> new RuntimeException("El rol '" + user.getRole() + "' no existe"));
        // 2. Mapear Domain User → UserEntity
        UserEntity userEntity = new UserEntity();
            userEntity.setName(user.getName());
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(user.getPassword());
            userEntity.setDirection(user.getDirection());
            userEntity.setPhone(user.getPhone());
            userEntity.setIsDeleted(false);
            userEntity.setRole(roleEntity);

        // 3. Guardar en la base de datos
        UserEntity savedEntity = UserRepository.save(userEntity);

        // 4. Mapear UserEntity → Domain User y retornar
        user.setId(savedEntity.getId());
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return UserRepository.existsByEmail(email);
    }

    @Override
    public long idUser(String email) {
        UserEntity userEntity = UserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userEntity.getId();
    }
}
