package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.EnableItemInput;
import com.letraaletra.api.features.items.application.output.EnableItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class EnableItemMapper {
    public static EnableItemInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new EnableItemInput(
                principal,
                itemId
        );
    }

    public static ItemResponse toResponse(EnableItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }
}
