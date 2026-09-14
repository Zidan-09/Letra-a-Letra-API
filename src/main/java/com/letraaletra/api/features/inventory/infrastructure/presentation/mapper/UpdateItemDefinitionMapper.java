package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.UpdateItemDefinitionRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class UpdateItemDefinitionMapper {
    public static UpdateItemDefinitionInput toInput(
            AuthenticatedUser principal,
            UUID itemId,
            UpdateItemDefinitionRequest request
    ) {
        return new UpdateItemDefinitionInput(
                principal,
                itemId,
                request.name(),
                request.assetPath(),
                request.available()
        );
    }

    public static ItemDefinitionResponse toResponse(UpdateItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }
}
