package com.arka.MSAuthentication.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direction;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^(3\\d{9}|\\d{7})$", message = "El teléfono debe ser válido, celular de 10 dígitos iniciando en 3 (3xxxxxxxxx) o fijo de 7 dígitos")
    private String phone;
    // Campo clave: el rol que se desea asignar.
    //private String roleName;
}
