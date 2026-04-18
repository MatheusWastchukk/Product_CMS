package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Nome da categoria e obrigatorio")
        String name
) {
}
