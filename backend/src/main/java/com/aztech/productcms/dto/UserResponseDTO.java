package com.aztech.productcms.dto;

public record UserResponseDTO(
        Long id,
        String name,
        String username,
        String email,
        String role
) {
}
