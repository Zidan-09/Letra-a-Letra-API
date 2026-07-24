package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.GetCosmeticsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
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
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class GetCosmeticsController {
    private final UseCase<GetCosmeticsInput, GetCosmeticsOutput> useCase;

    public GetCosmeticsController(
            UseCase<GetCosmeticsInput, GetCosmeticsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<PageResponse<CosmeticDTO>>> handle(
            Pageable pageable
    ) {
        GetCosmeticsInput input = GetCosmeticsMapper.toInput(pageable);

        GetCosmeticsOutput output = useCase.execute(input);

        PageResponse<CosmeticDTO> dto = GetCosmeticsMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
