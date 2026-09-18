package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.ListItemsInput;
import com.letraaletra.api.features.items.application.output.ListItemsOutput;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class ListItemsMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("name", "kind", "category", "available");

    public static ListItemsInput toInput(
            AuthenticatedUser principal,
            String kind,
            String category,
            Boolean available,
            Pageable pageable
    ) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new ListItemsInput(
                principal,
                parseKind(kind),
                parseCategory(category),
                available,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<ItemResponse> toResponse(ListItemsOutput output) {
        Page<Item> page = output.items();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(ItemResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
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

    private static EquippableCategory parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return EquippableCategory.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
    }
}
