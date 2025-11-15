package com.arka.MSAuthentication.domain.model.gateway;

import com.arka.MSAuthentication.domain.model.User;

import java.util.Optional;

public interface UserGateway {
    User SaveUser(User user);
    boolean existsByEmail(String email);
//    Optional<User> findById(Long id);
//    Optional<User> findByUsername(String username);
    long idUser(String email);
//    void deleteById(Long id);
//    boolean existsByUsername(String username);
//    boolean existsByEmail(String email);

    User BuscarUser(Long id);



}
