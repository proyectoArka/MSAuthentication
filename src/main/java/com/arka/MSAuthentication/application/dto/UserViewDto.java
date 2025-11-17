package com.arka.MSAuthentication.application.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserViewDto {
    private Long id;
    private String name;
    private String email;
    private String direction;
    private String phone;
    private String role;
}

