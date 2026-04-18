package com.aztech.productcms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDTO(
        @NotBlank(message = "Nome do produto e obrigatorio")
        String name,

        String description,

        @NotNull(message = "Preco e obrigatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "Preco nao pode ser negativo")
        BigDecimal price,

        @NotNull(message = "Categoria e obrigatoria")
        Long categoryId,

        @Valid
        List<ProductAttributeDTO> attributes
) {
}
