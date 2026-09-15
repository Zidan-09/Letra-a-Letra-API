package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.DeleteItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.DeleteItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.DeleteItemDefinitionMapper;
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
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class DeleteItemDefinitionController {
    private final UseCase<DeleteItemDefinitionInput, DeleteItemDefinitionOutput> useCase;

    @DeleteMapping(path = "/items/{itemId}")
    public ResponseEntity<SuccessResponse<ItemDefinitionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId
    ) {
        DeleteItemDefinitionInput input = DeleteItemDefinitionMapper.toInput(principal, itemId);

        DeleteItemDefinitionOutput output = useCase.execute(input);

        ItemDefinitionResponse dto = DeleteItemDefinitionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
