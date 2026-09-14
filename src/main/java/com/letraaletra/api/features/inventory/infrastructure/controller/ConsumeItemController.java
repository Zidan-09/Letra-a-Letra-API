package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.ConsumeItemRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.InventoryMovementsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.ConsumeItemMapper;
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
public class ConsumeItemController {
    private final UseCase<ConsumeItemInput, ConsumeItemOutput> useCase;

    @PostMapping(path = "/items/{itemId}/consume")
    public ResponseEntity<SuccessResponse<InventoryMovementsResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId,
            @Valid @RequestBody ConsumeItemRequest request
    ) {
        ConsumeItemInput input = ConsumeItemMapper.toInput(principal.auth(), itemId, request);

        ConsumeItemOutput output = useCase.execute(input);

        InventoryMovementsResponse dto = ConsumeItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
