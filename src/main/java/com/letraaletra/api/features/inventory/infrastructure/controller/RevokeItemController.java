package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.RevokeItemInput;
import com.letraaletra.api.features.inventory.application.output.RevokeItemOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.RevokeItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "Inventory", description = "Rotas do inventário genérico de itens do usuário")
public class RevokeItemController {
    private final UseCase<RevokeItemInput, RevokeItemOutput> useCase;

    @DeleteMapping(path = "/items/{itemId}")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId
    ) {
        RevokeItemInput input = RevokeItemMapper.toInput(principal, itemId);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
