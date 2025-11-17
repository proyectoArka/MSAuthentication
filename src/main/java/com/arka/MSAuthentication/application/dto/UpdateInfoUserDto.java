package com.arka.MSAuthentication.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInfoUserDto {
    // Opcional, si viene no debe ser vacío. Mantener libre de @NotBlank para permitir null.
    @Size(min = 1, message = "La dirección no puede estar vacía")
    private String direccion;

    @Pattern(regexp = "^$|(3\\d{9}|\\d{7})", message = "El teléfono debe ser válido en Colombia: celular 3xxxxxxxxx (10 dígitos) o fijo 7 dígitos")
    private String telefono;

    @Email(message = "El email no tiene un formato válido")
    private String email; // renombrado (antes 'Email')

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Size(min = 1, message = "El nombre no puede estar vacío")
    private String nombre;
}
