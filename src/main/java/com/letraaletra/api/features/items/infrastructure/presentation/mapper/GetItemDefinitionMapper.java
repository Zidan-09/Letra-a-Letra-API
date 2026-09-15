package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.GetItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.GetItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class GetItemDefinitionMapper {
    public static GetItemDefinitionInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new GetItemDefinitionInput(
                principal,
                itemId
        );
    }

    public static ItemDefinitionResponse toResponse(GetItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }
}
