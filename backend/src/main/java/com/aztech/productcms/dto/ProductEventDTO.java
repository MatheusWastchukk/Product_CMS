package com.aztech.productcms.dto;

import java.time.LocalDateTime;

public record ProductEventDTO(
        String action,
        Long productId,
        String productName,
        Long categoryId,
        String categoryName,
        LocalDateTime occurredAt
) {
}
