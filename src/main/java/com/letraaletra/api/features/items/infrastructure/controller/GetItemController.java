package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.GetItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catǭlogo de itens")
public class GetItemController {
    private final UseCase<GetItemInput, GetItemOutput> useCase;

    @GetMapping(path = "/items/{itemId}")
    public ResponseEntity<SuccessResponse<ItemResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId
    ) {
        GetItemInput input = GetItemMapper.toInput(principal, itemId);

        GetItemOutput output = useCase.execute(input);

        ItemResponse dto = GetItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
