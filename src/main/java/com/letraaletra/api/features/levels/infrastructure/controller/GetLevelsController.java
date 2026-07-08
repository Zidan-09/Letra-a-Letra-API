package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.GetLevelsResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.GetLevelsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
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
    public ResponseEntity<SuccessResponse<GetLevelsResponse>> handle(
            Pageable pageable
    ) {
        GetLevelsInput input = GetLevelsMapper.toInput(pageable);

        GetLevelsOutput output = useCase.execute(input);

        GetLevelsResponse dto = GetLevelsMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
