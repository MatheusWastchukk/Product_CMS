package com.aztech.productcms.dto;

public record AuthResponseDTO(
        String token,
        UserResponseDTO user
) {
}
