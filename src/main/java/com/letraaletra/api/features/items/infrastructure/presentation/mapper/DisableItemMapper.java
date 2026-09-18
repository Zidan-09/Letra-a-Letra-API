package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.DisableItemInput;
import com.letraaletra.api.features.items.application.output.DisableItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DisableItemMapper {
    public static DisableItemInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new DisableItemInput(
                principal,
                itemId
        );
    }

    public static ItemResponse toResponse(DisableItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }
}
