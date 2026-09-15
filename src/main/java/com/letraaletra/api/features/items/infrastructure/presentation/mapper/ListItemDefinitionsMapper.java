package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class ListItemDefinitionsMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("name", "kind", "category", "available");

    public static ListItemDefinitionsInput toInput(
            AuthenticatedUser principal,
            String kind,
            String category,
            Boolean available,
            Pageable pageable
    ) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new ListItemDefinitionsInput(
                principal,
                parseKind(kind),
                parseCategory(category),
                available,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<ItemDefinitionResponse> toResponse(ListItemDefinitionsOutput output) {
        Page<ItemDefinition> page = output.definitions();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(ItemDefinitionResponseMapper::toResponse)
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
}
