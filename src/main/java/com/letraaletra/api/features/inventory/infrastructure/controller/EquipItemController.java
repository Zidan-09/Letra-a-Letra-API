package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.EquipItemInput;
import com.letraaletra.api.features.inventory.application.output.EquipItemOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.EquipItemRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.InventoryMovementsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.EquipItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "Inventory", description = "Rotas do inventário genérico de itens do usuário")
public class EquipItemController {
    private final UseCase<EquipItemInput, EquipItemOutput> useCase;

    @PostMapping(path = "/items/{itemId}/equip")
    public ResponseEntity<SuccessResponse<InventoryMovementsResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId,
            @Valid @RequestBody EquipItemRequest request
    ) {
        EquipItemInput input = EquipItemMapper.toInput(principal.auth(), itemId, request);

        EquipItemOutput output = useCase.execute(input);

        InventoryMovementsResponse dto = EquipItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
