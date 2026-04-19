package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductAttributeDTO(
        @NotBlank(message = "Nome do atributo é obrigatório")
        String name,

        @NotBlank(message = "Valor do atributo é obrigatório")
        String value
) {
}
