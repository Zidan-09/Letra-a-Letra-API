package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.usecase.SignInUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.presentation.controller.ApiResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.SignInRequest;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponseDTO;
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
    private final SignInMapper signInMapper;

    public SignInController(SignInUseCase signInUseCase, SignInMapper signInMapper) {
        this.signInUseCase = signInUseCase;
        this.signInMapper = signInMapper;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        SignInInput command = signInMapper.toInput(request);

        SignInOutput output = signInUseCase.execute(command);

        SignInResponse dto = signInMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
