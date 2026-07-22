package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetMyProfileInput;
import com.letraaletra.api.features.user.application.output.GetMyProfileOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetMyProfileResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetMyProfileMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetMyProfileController {
    private final UseCase<GetMyProfileInput, GetMyProfileOutput> useCase;

    public GetMyProfileController(
            UseCase<GetMyProfileInput, GetMyProfileOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/me")
    public ResponseEntity<SuccessResponse<GetMyProfileResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetMyProfileInput input = GetMyProfileMapper.toInput(principal.auth());

        GetMyProfileOutput output = useCase.execute(input);

        GetMyProfileResponse dto = GetMyProfileMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
