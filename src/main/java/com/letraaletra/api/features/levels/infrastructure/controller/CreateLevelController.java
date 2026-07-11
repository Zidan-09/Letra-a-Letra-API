package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.CreateLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.CreateLevelMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/level")
@Tag(name = "Level", description = "Rotas relacionadas ao gerenciamento dos níveis e suas respectivas recompensas")
public class CreateLevelController {
    private final UseCase<CreateLevelInput, CreateLevelOutput> useCase;

    public CreateLevelController(
            UseCase<CreateLevelInput, CreateLevelOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<CreateLevelResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CreateLevelRequest request
    ) {
        CreateLevelInput input = CreateLevelMapper.toInput(principal.auth(), request);

        CreateLevelOutput output = useCase.execute(input);

        CreateLevelResponse dto = CreateLevelMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
