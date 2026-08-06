package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.UpdateCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.UpdateCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.UpdateCosmeticMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class UpdateCosmeticController {
    private final UseCase<UpdateCosmeticInput, UpdateCosmeticOutput> useCase;

    @Transactional
    @PutMapping("/{cosmeticId}")
    public ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateCosmeticRequest request,
            @PathVariable UUID cosmeticId
    ) {
        UpdateCosmeticInput input = UpdateCosmeticMapper.toInput(principal, request, cosmeticId);

        UpdateCosmeticOutput output = useCase.execute(input);

        UpdateCosmeticResponse dto = UpdateCosmeticMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
