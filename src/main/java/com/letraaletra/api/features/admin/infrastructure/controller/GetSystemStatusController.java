package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetSystemStatusInput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetSystemStatusResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetSystemStatusMapper;
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
public class GetSystemStatusController {
    private final UseCase<GetSystemStatusInput, GetSystemStatusOutput> useCase;

    public GetSystemStatusController(
            UseCase<GetSystemStatusInput, GetSystemStatusOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/system")
    public ResponseEntity<SuccessResponse<GetSystemStatusResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetSystemStatusInput input = GetSystemStatusMapper.toInput(principal.auth());

        GetSystemStatusOutput output = useCase.execute(input);

        GetSystemStatusResponse dto = GetSystemStatusMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
