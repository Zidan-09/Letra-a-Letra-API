package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetUserInventoryMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetUserInventoryController {
    private final UseCase<GetUserInventoryInput, GetUserInventoryOutput> useCase;

    @GetMapping(path = "/{userId}/inventory")
    public ResponseEntity<SuccessResponse<PageResponse<InventoryItemResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID userId,
            Pageable pageable
    ) {
        GetUserInventoryInput input = GetUserInventoryMapper.toInput(principal, userId, pageable);

        GetUserInventoryOutput output = useCase.execute(input);

        PageResponse<InventoryItemResponse> dto = GetUserInventoryMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
