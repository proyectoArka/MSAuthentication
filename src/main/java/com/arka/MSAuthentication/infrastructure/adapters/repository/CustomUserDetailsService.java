package com.arka.MSAuthentication.infrastructure.adapters.repository;

import com.arka.MSAuthentication.infrastructure.adapters.entity.PermissionEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        //Obtener y Mapear Permisos
        Collection<? extends GrantedAuthority> authorities = userEntity.getRole().getPermissions().stream()
                .map(PermissionEntity::getCode) // Obtener el código de permiso (ej: "productos:crear")
                .map(SimpleGrantedAuthority::new) // Convertir el código en una Authority
                .collect(Collectors.toSet());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities) // 🆕 Usar authorities() con los permisos
                .accountLocked(Boolean.TRUE.equals(userEntity.getIsDeleted()))
                .build();
    }
}

