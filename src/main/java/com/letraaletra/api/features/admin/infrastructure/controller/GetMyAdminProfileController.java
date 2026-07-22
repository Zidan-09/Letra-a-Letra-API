package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetMyAdminProfileInput;
import com.letraaletra.api.features.admin.application.output.GetMyAdminProfileOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetMyAdminProfileResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetMyAdminProfileMapper;
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
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class GetMyAdminProfileController {
    private final UseCase<GetMyAdminProfileInput, GetMyAdminProfileOutput> useCase;

    public GetMyAdminProfileController(
            UseCase<GetMyAdminProfileInput, GetMyAdminProfileOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/me")
    public ResponseEntity<SuccessResponse<GetMyAdminProfileResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetMyAdminProfileInput input = GetMyAdminProfileMapper.toInput(principal.auth());

        GetMyAdminProfileOutput output = useCase.execute(input);

        GetMyAdminProfileResponse dto = GetMyAdminProfileMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
