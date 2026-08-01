package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetAdminsInput;
import com.letraaletra.api.features.admin.application.output.GetAdminsOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.AdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetAdminsMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
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
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class GetAdminsController {
    private final UseCase<GetAdminsInput, GetAdminsOutput> useCase;

    public GetAdminsController(
            UseCase<GetAdminsInput, GetAdminsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<PageResponse<AdminResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetAdminsInput input = GetAdminsMapper.toInput(principal, pageable);

        GetAdminsOutput output = useCase.execute(input);

        PageResponse<AdminResponse> dto = GetAdminsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
