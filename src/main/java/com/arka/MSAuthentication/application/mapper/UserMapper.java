package com.arka.MSAuthentication.application.mapper;

import com.arka.MSAuthentication.application.dto.UserCreateDto;
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

    public UserCreateDto toDto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDirection(user.getDirection());
        dto.setPhone(user.getPhone());
        return dto;
    }
}