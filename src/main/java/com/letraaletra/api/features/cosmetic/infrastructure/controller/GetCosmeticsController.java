package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.GetCosmeticsUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.GetCosmeticsResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.GetCosmeticsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/cosmetic")
public class GetCosmeticsController {
    private final GetCosmeticsUseCase useCase;

    public GetCosmeticsController(
            GetCosmeticsUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<GetCosmeticsResponse>> handle(
            Pageable pageable
    ) {
        GetCosmeticsInput input = GetCosmeticsMapper.toInput(pageable);

        GetCosmeticsOutput output = useCase.execute(input);

        GetCosmeticsResponse dto = GetCosmeticsMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
