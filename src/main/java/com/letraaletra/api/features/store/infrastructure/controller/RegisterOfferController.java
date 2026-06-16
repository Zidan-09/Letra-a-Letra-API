package com.letraaletra.api.features.store.infrastructure.controller;

import com.letraaletra.api.features.store.application.input.RegisterOfferInput;
import com.letraaletra.api.features.store.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.store.application.usecase.RegisterOfferUseCase;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.RegisterOfferResponse;
import com.letraaletra.api.features.store.infrastructure.presentation.mapper.RegisterOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterOfferController {
    private final RegisterOfferUseCase useCase;

    public RegisterOfferController(
            RegisterOfferUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/store")
    public ResponseEntity<SuccessResponse<RegisterOfferResponse>> registerOffer(@Valid @RequestBody RegisterOfferRequest request) {
        RegisterOfferInput input = RegisterOfferMapper.toInput(request);

        RegisterOfferOutput output = useCase.execute(input);

        RegisterOfferResponse dto = RegisterOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
