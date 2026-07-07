package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.RegisterCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.RegisterCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class RegisterCosmeticController {
    private final RegisterCosmeticUseCase useCase;

    public RegisterCosmeticController(
            RegisterCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> registerCosmetic(
            @AuthenticationPrincipal UUID auth,
            @Valid @ModelAttribute RegisterCosmeticRequest request
    ) {
        RegisterCosmeticInput input = RegisterCosmeticMapper.toInput(auth, request);

        RegisterCosmeticOutput output = useCase.execute(input);

        RegisterCosmeticResponse dto = RegisterCosmeticMapper.toResponse(output);

       return ApiResponseService.success(dto);
    }
}
