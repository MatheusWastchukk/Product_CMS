package com.aztech.productcms.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CategoryAttributeRequestDTO(
        @NotEmpty(message = "Informe ao menos um atributo")
        List<String> attributes
) {
}
