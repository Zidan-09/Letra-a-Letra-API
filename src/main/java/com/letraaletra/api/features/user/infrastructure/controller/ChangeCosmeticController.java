package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class ChangeCosmeticController {
    private final UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> changeCosmeticUseCase;

    public ChangeCosmeticController(
            UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> changeCosmeticUseCase
    ) {
        this.changeCosmeticUseCase = changeCosmeticUseCase;
    }

    @PatchMapping(path = "/cosmetic/{cosmeticId}")
    public ResponseEntity<SuccessResponse<ChangeCosmeticResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @PathVariable UUID cosmeticId
    ) {
        ChangeCosmeticInput input = ChangeCosmeticMapper.toInput(cosmeticId, auth);

        ChangeCosmeticOutput output = changeCosmeticUseCase.execute(input);

        ChangeCosmeticResponse dto = ChangeCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
