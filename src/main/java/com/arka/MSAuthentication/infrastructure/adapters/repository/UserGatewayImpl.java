package com.arka.MSAuthentication.infrastructure.adapters.repository;

import com.arka.MSAuthentication.domain.model.Exception.BusinessRuleException;
import com.arka.MSAuthentication.domain.model.Exception.ResourceNotFoundException;
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
        // 1. Buscar el rol (esta lógica es correcta)
        RoleEntity roleEntity = roleRepository.findByNameRole(user.getRole())
                .orElseThrow(() -> new BusinessRuleException("El rol '" + user.getRole() + "' no existe"));

        UserEntity userEntity;

        if (user.getId() != null) {
            // ******* 🔑 CASO 1: ACTUALIZACIÓN *******
            // 2. Fetch la entidad existente para preservar el 'createdAt'
            userEntity = UserRepository.findById(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", user.getId()));
        } else {
            // ******* 🔑 CASO 2: INSERCIÓN NUEVA *******
            // 2. Crear una nueva entidad
            userEntity = new UserEntity();
        }

        // 4. Mapear/Actualizar todos los campos mutables
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setDirection(user.getDirection());
        userEntity.setPhone(user.getPhone());
        userEntity.setIsDeleted(user.getIsDeleted());
        userEntity.setRole(roleEntity);

        // 5. Guardar (JPA sabe que es UPDATE porque el objeto ya tiene ID)
        UserEntity savedEntity = UserRepository.save(userEntity);

        // 6. Mapear de vuelta
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email: " + email + " no encontrado"));
        return userEntity.getId();
    }

    @Override
    public User BuscarUser(Long id) {
        UserEntity userEntity = UserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        User user = new User();
            user.setRole(userEntity.getRole().getNameRole());
            user.setId(userEntity.getId());
            user.setName(userEntity.getName());
            user.setEmail(userEntity.getEmail());
            user.setDirection(userEntity.getDirection());
            user.setPhone(userEntity.getPhone());
            user.setIsDeleted(userEntity.getIsDeleted());
            user.setPassword(userEntity.getPassword());
        return user;
    }
}
