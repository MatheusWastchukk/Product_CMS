package com.aztech.productcms.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        List<ProductAttributeDTO> attributes
) {
}
