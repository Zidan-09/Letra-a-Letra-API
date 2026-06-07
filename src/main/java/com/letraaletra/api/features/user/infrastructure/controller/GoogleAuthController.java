package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.usecase.GoogleAuthUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.GoogleAuthRequest;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GoogleAuthMapper;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.SignInMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {
    private final GoogleAuthUseCase googleAuthUseCase;

    public GoogleAuthController(GoogleAuthUseCase googleAuthUseCase) {
        this.googleAuthUseCase = googleAuthUseCase;
    }

    @PostMapping("/google")
    public ResponseEntity<SuccessResponse<SignInResponse>> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        AuthInput input = GoogleAuthMapper.toInput(request);

        SignInOutput output = googleAuthUseCase.execute(input);

        SignInResponse dto = SignInMapper.toResponse(output);

        return ApiResponseService.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
