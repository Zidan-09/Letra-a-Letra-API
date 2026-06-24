package com.letraaletra.api.features.store.infrastructure.controller;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.request.BuyStoreOfferRequest;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.BuyStoreOfferResponse;
import com.letraaletra.api.features.store.infrastructure.presentation.mapper.BuyStoreOfferMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyStoreOfferController {
    private final BuyOfferUseCase useCase;

    public BuyStoreOfferController(
            BuyOfferUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/store/buy")
    public ResponseEntity<SuccessResponse<BuyStoreOfferResponse>> buyOffer(
            @AuthenticationPrincipal User user,
            @RequestBody @NotBlank BuyStoreOfferRequest request
    ) {
        BuyOfferInput input = BuyStoreOfferMapper.toInput(user.getId().toString(), request.offerId());

        BuyOfferOutput output = useCase.execute(input);

        BuyStoreOfferResponse dto = BuyStoreOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
