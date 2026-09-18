package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class GetItemMapper {
    public static GetItemInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new GetItemInput(
                principal,
                itemId
        );
    }

    public static ItemResponse toResponse(GetItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }
}
