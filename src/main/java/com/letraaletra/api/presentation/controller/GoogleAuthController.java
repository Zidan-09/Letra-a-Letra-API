package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.command.auth.AuthCommand;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.application.usecase.auth.GoogleAuthUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.presentation.dto.request.user.GoogleAuthRequestDTO;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponseDTO;
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
@RequestMapping("/auth")
public class GoogleAuthController {

    @Autowired
    private GoogleAuthUseCase googleAuthUseCase;

    @Autowired
    private GoogleAuthMapper googleAuthMapper;

    @Autowired
    private SignInMapper signInMapper;

    @PostMapping("/google")
    public ResponseEntity<SuccessResponseDTO<SignInResponse>> googleLogin(@Valid @RequestBody GoogleAuthRequestDTO request) {
        AuthCommand command = googleAuthMapper.toCommand(request);

        SignInOutput output = googleAuthUseCase.execute(command);

        SignInResponse dto = signInMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
