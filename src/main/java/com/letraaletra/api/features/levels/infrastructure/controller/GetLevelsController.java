package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.level.LevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.GetLevelsMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/level")
@Tag(name = "Level", description = "Rotas relacionadas ao gerenciamento dos níveis e suas respectivas recompensas")
public class GetLevelsController {
    private final UseCase<GetLevelsInput, GetLevelsOutput> useCase;

    public GetLevelsController(
            UseCase<GetLevelsInput, GetLevelsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<PageResponse<LevelResponse>>> handle(
            Pageable pageable
    ) {
        GetLevelsInput input = GetLevelsMapper.toInput(pageable);

        GetLevelsOutput output = useCase.execute(input);

        PageResponse<LevelResponse> dto = GetLevelsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
