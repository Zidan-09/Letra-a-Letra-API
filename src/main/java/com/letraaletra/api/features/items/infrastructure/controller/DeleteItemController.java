package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.DeleteItemInput;
import com.letraaletra.api.features.items.application.output.DeleteItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.DeleteItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catǭlogo de itens")
public class DeleteItemController {
    private final UseCase<DeleteItemInput, DeleteItemOutput> useCase;

    @DeleteMapping(path = "/items/{itemId}")
    public ResponseEntity<SuccessResponse<ItemResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId
    ) {
        DeleteItemInput input = DeleteItemMapper.toInput(principal, itemId);

        DeleteItemOutput output = useCase.execute(input);

        ItemResponse dto = DeleteItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
