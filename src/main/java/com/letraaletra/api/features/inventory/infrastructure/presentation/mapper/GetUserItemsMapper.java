package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.GetUserItemsResponse;

import java.util.UUID;

public class GetUserItemsMapper {
    public static GetUserItemsInput toInput(
            UUID ownerId,
            String kind,
            String category,
            String context,
            Boolean equipped
    ) {
        return new GetUserItemsInput(
                ownerId,
                parseKind(kind),
                parseCategory(category),
                parseContext(context),
                equipped
        );
    }

    public static GetUserItemsResponse toResponse(GetUserItemsOutput output) {
        return new GetUserItemsResponse(
                output.items().stream()
                        .map(UserItemResponseMapper::toResponse)
                        .toList()
        );
    }

    private static ItemKind parseKind(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return ItemKind.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
    }

    private static ItemCategory parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return ItemCategory.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
    }

    private static EquippableContext parseContext(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return EquippableContext.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
    }
}
