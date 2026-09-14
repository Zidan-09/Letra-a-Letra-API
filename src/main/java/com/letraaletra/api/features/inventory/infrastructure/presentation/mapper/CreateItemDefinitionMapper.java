package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemEffect;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.CreateItemDefinitionRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class CreateItemDefinitionMapper {
    public static CreateItemDefinitionInput toInput(AuthenticatedUser principal, CreateItemDefinitionRequest request) {
        return new CreateItemDefinitionInput(
                principal,
                request.name(),
                request.kind(),
                request.category(),
                request.applicability(),
                request.stackable(),
                request.maxStack(),
                request.consumable(),
                toEffect(request.effect()),
                request.assetPath()
        );
    }

    public static ItemDefinitionResponse toResponse(CreateItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }

    private static ItemEffect toEffect(CreateItemDefinitionRequest.ItemEffectRequest request) {
        if (request == null) {
            return null;
        }

        return new ItemEffect(request.type(), request.magnitude(), request.durationMinutes());
    }
}
