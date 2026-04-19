package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryAttributeDefinitionDTO(
        @NotBlank(message = "Nome do atributo e obrigatorio")
        String name,

        @NotBlank(message = "Tipo do atributo e obrigatorio")
        String type
) {
}
