package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.FindLevelByValueInput;
import com.letraaletra.api.features.levels.application.output.FindLevelByValueOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.FindLevelByValueResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.FindLevelByValueMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/level")
@Tag(name = "Level", description = "Rotas relacionadas ao gerenciamento dos níveis e suas respectivas recompensas")
public class FindLevelByValueController {
    private final UseCase<FindLevelByValueInput, FindLevelByValueOutput> useCase;

    public FindLevelByValueController(
            UseCase<FindLevelByValueInput, FindLevelByValueOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/value/{value}")
    public ResponseEntity<SuccessResponse<FindLevelByValueResponse>> handle(
            @PathVariable Integer value
    ) {
        FindLevelByValueInput input = FindLevelByValueMapper.toInput(value);

        FindLevelByValueOutput output = useCase.execute(input);

        FindLevelByValueResponse dto = FindLevelByValueMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
