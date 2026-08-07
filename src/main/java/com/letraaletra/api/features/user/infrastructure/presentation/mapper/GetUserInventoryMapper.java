package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class GetUserInventoryMapper {
    public static GetUserInventoryInput toInput(AuthenticatedUser principal, UUID userId, Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetUserInventoryInput(
                principal,
                userId,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<InventoryItemResponse> toResponse(GetUserInventoryOutput output) {
        Page<InventoryItem> page = output.inventory();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(InventoryItemResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
