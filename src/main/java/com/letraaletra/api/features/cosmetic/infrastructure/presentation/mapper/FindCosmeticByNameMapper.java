package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.FindCosmeticByNameResponse;

public class FindCosmeticByNameMapper {
    public static FindCosmeticByNameInput toInput(String name) {
        return new FindCosmeticByNameInput(
                name
        );
    }

    public static FindCosmeticByNameResponse toResponse(FindCosmeticByNameOutput output) {
        return new FindCosmeticByNameResponse(
                CosmeticResponseMapper.toDto(output.cosmetic())
        );
    }
}
