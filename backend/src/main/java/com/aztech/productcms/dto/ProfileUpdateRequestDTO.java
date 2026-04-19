package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        String password
) {
}
