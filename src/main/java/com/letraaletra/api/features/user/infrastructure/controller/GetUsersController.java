package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetUsersInput;
import com.letraaletra.api.features.user.application.output.GetUsersOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetUsersMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetUsersController {
    private final UseCase<GetUsersInput, GetUsersOutput> useCase;

    public GetUsersController(
            UseCase<GetUsersInput, GetUsersOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<PageResponse<UserResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetUsersInput input = GetUsersMapper.toInput(principal.auth(), pageable);

        GetUsersOutput output = useCase.execute(input);

        PageResponse<UserResponse> dto = GetUsersMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
