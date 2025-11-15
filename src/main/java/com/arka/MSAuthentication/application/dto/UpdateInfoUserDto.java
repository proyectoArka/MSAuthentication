package com.arka.MSAuthentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInfoUserDto {
    private String direccion;
    private String telefono;
    private String Email;
    private String password;
    private String nombre;
}
