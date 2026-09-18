package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.DisableItemInput;
import com.letraaletra.api.features.items.application.output.DisableItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.DisableItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class DisableItemController {
    private final UseCase<DisableItemInput, DisableItemOutput> useCase;

    @PatchMapping(path = "/items/{itemId}/disable")
    public ResponseEntity<SuccessResponse<ItemResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId
    ) {
        DisableItemInput input = DisableItemMapper.toInput(principal, itemId);

        DisableItemOutput output = useCase.execute(input);

        ItemResponse dto = DisableItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
