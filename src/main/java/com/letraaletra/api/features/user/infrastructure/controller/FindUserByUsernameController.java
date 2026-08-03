package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.FindUserByUsernameResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.FindUserByUsernameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class FindUserByUsernameController {
    private final UseCase<FindUserByUsernameInput, FindUserByUsernameOutput> useCase;

    @GetMapping(path = "/username/{username}")
    public ResponseEntity<SuccessResponse<FindUserByUsernameResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String username
    ) {
        FindUserByUsernameInput input = FindUserByUsernameMapper.toInput(principal, username);

        FindUserByUsernameOutput output = useCase.execute(input);

        FindUserByUsernameResponse dto = FindUserByUsernameMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
