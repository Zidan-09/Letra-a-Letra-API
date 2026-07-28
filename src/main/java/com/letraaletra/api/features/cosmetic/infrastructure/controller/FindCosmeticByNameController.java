package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.FindCosmeticByNameResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.FindCosmeticByNameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class FindCosmeticByNameController {
    private final UseCase<FindCosmeticByNameInput, FindCosmeticByNameOutput> useCase;

    public FindCosmeticByNameController(
            UseCase<FindCosmeticByNameInput, FindCosmeticByNameOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> handle(
            @PathVariable @NotBlank String name
    ) {
        FindCosmeticByNameInput input = FindCosmeticByNameMapper.toInput(name);

        FindCosmeticByNameOutput output = useCase.execute(input);

        FindCosmeticByNameResponse dto = FindCosmeticByNameMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
