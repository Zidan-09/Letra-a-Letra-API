package com.letraaletra.api.features.store.infrastructure.controller;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.request.BuyStoreOfferRequest;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.BuyStoreOfferResponse;
import com.letraaletra.api.features.store.infrastructure.presentation.mapper.BuyStoreOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
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
public class BuyStoreOfferController {
    private final BuyOfferUseCase useCase;

    public BuyStoreOfferController(
            BuyOfferUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/buy")
    public ResponseEntity<SuccessResponse<BuyStoreOfferResponse>> buyOffer(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody BuyStoreOfferRequest request
    ) {
        BuyOfferInput input = BuyStoreOfferMapper.toInput(auth, request.offerId());

        BuyOfferOutput output = useCase.execute(input);

        BuyStoreOfferResponse dto = BuyStoreOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
