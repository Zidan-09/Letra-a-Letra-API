package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DisableCosmeticMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class DisableCosmeticController {
    private final UseCase<DisableCosmeticInput, DisableCosmeticOutput> useCase;

    @Transactional
    @PatchMapping(path = "/disable/{cosmeticId}")
    public ResponseEntity<SuccessResponse<DisableCosmeticResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID cosmeticId
    ) {
        DisableCosmeticInput input = DisableCosmeticMapper.toInput(principal, cosmeticId);

        DisableCosmeticOutput output = useCase.execute(input);

        DisableCosmeticResponse dto = DisableCosmeticMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
