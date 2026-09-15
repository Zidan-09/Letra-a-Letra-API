package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.DeleteItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.DeleteItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DeleteItemDefinitionMapper {
    public static DeleteItemDefinitionInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new DeleteItemDefinitionInput(
                principal,
                itemId
        );
    }

    public static ItemDefinitionResponse toResponse(DeleteItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }
}
