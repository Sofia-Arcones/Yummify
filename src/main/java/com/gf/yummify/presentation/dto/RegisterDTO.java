package com.gf.yummify.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    @NotNull(message = "El nombre de usuario no puede ser nulo")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @NotNull(message = "La contraseña no puede ser nula")
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotNull(message = "El correo electrónico no puede ser nulo")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;
}
