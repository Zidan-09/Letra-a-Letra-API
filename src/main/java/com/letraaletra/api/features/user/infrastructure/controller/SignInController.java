package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.usecase.SignInUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.presentation.controller.ApiResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.SignInRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.SignInMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
public class SignInController {
    private final SignInUseCase signInUseCase;

    public SignInController(SignInUseCase signInUseCase) {
        this.signInUseCase = signInUseCase;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        SignInInput input = SignInMapper.toInput(request);

        SignInOutput output = signInUseCase.execute(input);

        SignInResponse dto = SignInMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
