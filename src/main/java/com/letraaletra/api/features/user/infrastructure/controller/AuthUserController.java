package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.usecase.AuthUserUseCase;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.AuthUserRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.AuthUserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
public class AuthUserController {
    private final AuthUserUseCase authUserUseCase;

    public AuthUserController(AuthUserUseCase authUserUseCase) {
        this.authUserUseCase = authUserUseCase;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<AuthUserResponse>> signIn(@Valid @RequestBody AuthUserRequest request) {
        SignInInput input = AuthUserMapper.toInput(request);

        SignInOutput output = authUserUseCase.execute(input);

        AuthUserResponse dto = AuthUserMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
