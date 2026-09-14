package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.UpdateItemDefinitionRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.UpdateItemDefinitionMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Inventory", description = "Rotas do inventário genérico de itens do usuário")
public class UpdateItemDefinitionController {
    private final UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> useCase;

    @PutMapping(path = "/items/{itemId}")
    public ResponseEntity<SuccessResponse<ItemDefinitionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId,
            @RequestBody UpdateItemDefinitionRequest request
    ) {
        UpdateItemDefinitionInput input = UpdateItemDefinitionMapper.toInput(principal, itemId, request);

        UpdateItemDefinitionOutput output = useCase.execute(input);

        ItemDefinitionResponse dto = UpdateItemDefinitionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
