package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.ToggleItemAvailabilityRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class ToggleItemAvailabilityMapper {
    public static ToggleItemAvailabilityInput toInput(
            AuthenticatedUser principal,
            UUID itemId,
            ToggleItemAvailabilityRequest request
    ) {
        return new ToggleItemAvailabilityInput(
                principal,
                itemId,
                request.available()
        );
    }

    public static ItemDefinitionResponse toResponse(ToggleItemAvailabilityOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }
}
