package com.aztech.audit.dto;

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
