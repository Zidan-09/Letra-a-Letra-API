package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.RegisterCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.RegisterCosmeticMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/cosmetic")
public class RegisterCosmeticController {
    private final RegisterCosmeticUseCase useCase;

    public RegisterCosmeticController(
            RegisterCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> registerCosmetic(
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute RegisterCosmeticRequest request
    ) {
        validateUser(user);

        RegisterCosmeticInput input = RegisterCosmeticMapper.toInput(request);

        RegisterCosmeticOutput output = useCase.execute(input);

        RegisterCosmeticResponse dto = RegisterCosmeticMapper.toResponse(output);

       return ApiResponseService.success(dto);
    }

    private void validateUser(User user) {
        if (!user.isAdmin()) {
            throw new UserIsNotAdminException();
        }
    }
}
