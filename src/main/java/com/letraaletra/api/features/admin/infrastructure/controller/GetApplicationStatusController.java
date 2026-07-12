package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetApplicationStatusInput;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetApplicationStatusResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetApplicationStatusMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class GetApplicationStatusController {
    private final UseCase<GetApplicationStatusInput, GetApplicationStatusOutput> useCase;

    public GetApplicationStatusController(
            UseCase<GetApplicationStatusInput, GetApplicationStatusOutput> useCase
    ) {
        this.useCase = useCase;
    }

    public ResponseEntity<SuccessResponse<GetApplicationStatusResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetApplicationStatusInput input = GetApplicationStatusMapper.toInput(principal.auth());

        GetApplicationStatusOutput output = useCase.execute(input);

        GetApplicationStatusResponse dto = GetApplicationStatusMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
