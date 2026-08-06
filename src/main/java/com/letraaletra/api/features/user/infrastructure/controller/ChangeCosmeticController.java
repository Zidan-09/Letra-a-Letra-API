package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeCosmeticMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class ChangeCosmeticController {
    private final UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> useCase;

    @Transactional
    @PatchMapping(path = "/cosmetic/{cosmeticId}")
    public ResponseEntity<SuccessResponse<ChangeCosmeticResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID cosmeticId
    ) {
        ChangeCosmeticInput input = ChangeCosmeticMapper.toInput(cosmeticId, principal.auth());

        ChangeCosmeticOutput output = useCase.execute(input);

        ChangeCosmeticResponse dto = ChangeCosmeticMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
