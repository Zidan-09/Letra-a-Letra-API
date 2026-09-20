package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.RefreshSessionRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.RefreshSessionMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class RefreshSessionController {
    private final UseCase<RefreshSessionInput, RefreshSessionOutput> useCase;

    @PostMapping(path = "/auth/refresh")
    public ResponseEntity<SuccessResponse<AuthUserResponse>> handle(
            @Valid @RequestBody RefreshSessionRequest request
    ) {
        RefreshSessionInput input = RefreshSessionMapper.toInput(request);

        RefreshSessionOutput output = useCase.execute(input);

        AuthUserResponse dto = RefreshSessionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
