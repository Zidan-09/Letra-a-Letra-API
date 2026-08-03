package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.UpdateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.UpdateLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.UpdateLevelMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/level")
@Tag(name = "Level", description = "Rotas relacionadas ao gerenciamento dos níveis e suas respectivas recompensas")
public class UpdateLevelController {
    private final UseCase<UpdateLevelInput, UpdateLevelOutput> useCase;

    @PutMapping(path = "/{levelId}")
    public ResponseEntity<SuccessResponse<UpdateLevelResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID levelId,
            @Valid @RequestBody UpdateLevelRequest request
    ) {
        UpdateLevelInput input = UpdateLevelMapper.toInput(principal, levelId, request);

        UpdateLevelOutput output = useCase.execute(input);

        UpdateLevelResponse dto = UpdateLevelMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
