package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.FindLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.FindLevelMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/level")
@Tag(name = "Level", description = "Rotas relacionadas ao gerenciamento dos níveis e suas respectivas recompensas")
public class FindLevelController {
    private final UseCase<FindLevelInput, FindLevelOutput> useCase;

    public FindLevelController(
            UseCase<FindLevelInput, FindLevelOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/{levelId}")
    public ResponseEntity<SuccessResponse<FindLevelResponse>> handle(
            @PathVariable UUID levelId
    ) {
        FindLevelInput input = FindLevelMapper.toInput(levelId);

        FindLevelOutput output = useCase.execute(input);

        FindLevelResponse dto = FindLevelMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
