package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryAttributeDefinitionDTO(
        @NotBlank(message = "Nome do atributo é obrigatório")
        String name,

        @NotBlank(message = "Tipo do atributo é obrigatório")
        String type
) {
}
