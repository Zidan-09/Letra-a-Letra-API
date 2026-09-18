package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.ListItemsInput;
import com.letraaletra.api.features.items.application.output.ListItemsOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.ListItemsMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catǭlogo de itens")
public class ListItemsController {
    private final UseCase<ListItemsInput, ListItemsOutput> useCase;

    @GetMapping(path = "/items")
    public ResponseEntity<SuccessResponse<PageResponse<ItemResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            Pageable pageable
    ) {
        ListItemsInput input = ListItemsMapper.toInput(
                principal, kind, category, available, pageable);

        ListItemsOutput output = useCase.execute(input);

        PageResponse<ItemResponse> dto = ListItemsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
