package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.RegisterOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.RegisterOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/store")
@Tag(name = "Store", description = "Rotas relacionadas ao gerenciamento da loja do jogo")
public class RegisterOfferController {
    private final UseCase<RegisterOfferInput, RegisterOfferOutput> useCase;

    public RegisterOfferController(
            UseCase<RegisterOfferInput, RegisterOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<RegisterOfferResponse>> registerOffer(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody RegisterOfferRequest request
    ) {
        RegisterOfferInput input = RegisterOfferMapper.toInput(auth, request);

        RegisterOfferOutput output = useCase.execute(input);

        RegisterOfferResponse dto = RegisterOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }


}
