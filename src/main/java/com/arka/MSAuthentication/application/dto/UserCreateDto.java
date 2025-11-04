package com.arka.MSAuthentication.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String name;
    private String email;
    private String password;
    private String direction;
    private String phone;
    // Campo clave: el rol que se desea asignar.
    //private String roleName;
}
