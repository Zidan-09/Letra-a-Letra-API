package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.ListItemDefinitionsMapper;
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
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class ListItemDefinitionsController {
    private final UseCase<ListItemDefinitionsInput, ListItemDefinitionsOutput> useCase;

    @GetMapping(path = "/items")
    public ResponseEntity<SuccessResponse<PageResponse<ItemDefinitionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            Pageable pageable
    ) {
        ListItemDefinitionsInput input = ListItemDefinitionsMapper.toInput(
                principal, kind, category, available, pageable);

        ListItemDefinitionsOutput output = useCase.execute(input);

        PageResponse<ItemDefinitionResponse> dto = ListItemDefinitionsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
