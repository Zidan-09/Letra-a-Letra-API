package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.DeleteItemInput;
import com.letraaletra.api.features.items.application.output.DeleteItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DeleteItemMapper {
    public static DeleteItemInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new DeleteItemInput(
                principal,
                itemId
        );
    }

    public static ItemResponse toResponse(DeleteItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }
}
