package com.arka.MSAuthentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultUserDto {
    private String name;
    private String email;
    private String direccion;
    private String telefono;
}
