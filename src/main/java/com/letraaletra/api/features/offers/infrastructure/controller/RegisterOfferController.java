package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.RegisterOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.RegisterOfferMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/offer")
@Tag(name = "Offer", description = "Rotas relacionadas ao gerenciamento de ofertas da loja")
public class RegisterOfferController {
    private final UseCase<RegisterOfferInput, RegisterOfferOutput> useCase;

    @PostMapping()
    public ResponseEntity<SuccessResponse<RegisterOfferResponse>> registerOffer(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody RegisterOfferRequest request
    ) {
        RegisterOfferInput input = RegisterOfferMapper.toInput(principal, request);

        RegisterOfferOutput output = useCase.execute(input);

        RegisterOfferResponse dto = RegisterOfferMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
