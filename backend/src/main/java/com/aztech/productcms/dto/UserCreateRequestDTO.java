package com.aztech.productcms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Usuário é obrigatório")
        String username,

        @Email(message = "E-mail inválido")
        String email
) {
}
