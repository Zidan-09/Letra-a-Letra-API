package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.command.auth.AuthInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.application.usecase.auth.GoogleAuthUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.presentation.dto.request.user.GoogleAuthRequestDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;
import com.letraaletra.api.presentation.mappers.user.GoogleAuthMapper;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.SignInMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Autowired
    private GoogleAuthUseCase googleAuthUseCase;

    @Autowired
    private GoogleAuthMapper googleAuthMapper;

    @PostMapping("/google")
    public ResponseEntity<SuccessResponse<SignInResponse>> googleLogin(@Valid @RequestBody GoogleAuthRequestDTO request) {
        AuthInput input = googleAuthMapper.toCommand(request);

        SignInOutput output = googleAuthUseCase.execute(input);

        SignInResponse dto = SignInMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
