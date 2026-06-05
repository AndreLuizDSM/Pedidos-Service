package com.andre.pedidosservice.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank(message = "Nome obrigatorio") String name,
        @NotBlank(message = "Email obrigatorio")
        @Email(message = "Email deve conter @") String email,
        @NotBlank(message = "Senha obrigatoria") String password
) {
}
