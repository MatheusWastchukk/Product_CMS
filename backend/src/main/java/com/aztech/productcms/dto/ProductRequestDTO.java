package com.aztech.productcms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDTO(
        @NotBlank(message = "Nome do produto é obrigatório")
        String name,

        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "Preço não pode ser negativo")
        BigDecimal price,

        @NotNull(message = "Categoria é obrigatória")
        Long categoryId,

        @Valid
        List<ProductAttributeDTO> attributes
) {
}
