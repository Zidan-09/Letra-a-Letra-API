package com.letraaletra.api.features.store.infrastructure.controller;

import com.letraaletra.api.features.store.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.store.application.usecase.GetActiveOffersUseCase;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.GetActiveOffersResponse;
import com.letraaletra.api.features.store.infrastructure.presentation.mapper.GetActiveOffersMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/store")
@Tag(name = "Store", description = "Rotas relacionadas ao gerenciamento da loja do jogo")
public class GetActiveOffersController {
    private final GetActiveOffersUseCase useCase;

    public GetActiveOffersController(
            GetActiveOffersUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<GetActiveOffersResponse>> getActiveOffers() {
        GetActiveOffersOutput output = useCase.execute(null);

        GetActiveOffersResponse dto = GetActiveOffersMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
