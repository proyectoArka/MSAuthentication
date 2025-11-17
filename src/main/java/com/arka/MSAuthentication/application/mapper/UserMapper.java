package com.arka.MSAuthentication.application.mapper;

import com.arka.MSAuthentication.application.dto.UserCreateDto;
import com.arka.MSAuthentication.application.dto.UserViewDto;
import com.arka.MSAuthentication.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDirection(dto.getDirection());
        user.setPhone(dto.getPhone());
        return user;
    }

    // Este DTO de creación no expone la contraseña al devolverlo
    public UserCreateDto toDto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDirection(user.getDirection());
        dto.setPhone(user.getPhone());
        return dto;
    }

    public UserViewDto toViewDto(User user) {
        UserViewDto dto = new UserViewDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDirection(user.getDirection());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }
}