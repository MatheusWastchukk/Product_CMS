package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductAttributeDTO(
        @NotBlank(message = "Nome do atributo e obrigatorio")
        String name,

        @NotBlank(message = "Valor do atributo e obrigatorio")
        String value
) {
}
