package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.ToggleItemAvailabilityRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.ToggleItemAvailabilityMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class ToggleItemAvailabilityController {
    private final UseCase<ToggleItemAvailabilityInput, ToggleItemAvailabilityOutput> useCase;

    @PatchMapping(path = "/items/{itemId}/availability")
    public ResponseEntity<SuccessResponse<ItemDefinitionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId,
            @Valid @RequestBody ToggleItemAvailabilityRequest request
    ) {
        ToggleItemAvailabilityInput input = ToggleItemAvailabilityMapper.toInput(principal, itemId, request);

        ToggleItemAvailabilityOutput output = useCase.execute(input);

        ItemDefinitionResponse dto = ToggleItemAvailabilityMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
