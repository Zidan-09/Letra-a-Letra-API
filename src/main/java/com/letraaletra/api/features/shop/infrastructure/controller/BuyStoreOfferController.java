package com.letraaletra.api.features.shop.infrastructure.controller;

import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.infrastructure.presentation.dto.response.BuyStoreOfferResponse;
import com.letraaletra.api.features.shop.infrastructure.presentation.mapper.BuyStoreOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/shop/offers")
@Tag(name = "Shop", description = "Rotas relacionadas a funcionalidade da loja do jogo")
public class BuyStoreOfferController {
    private final UseCase<BuyOfferInput, BuyOfferOutput> useCase;

    public BuyStoreOfferController(
            UseCase<BuyOfferInput, BuyOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/{offerId}/buy")
    public ResponseEntity<SuccessResponse<BuyStoreOfferResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID offerId
    ) {
        BuyOfferInput input = BuyStoreOfferMapper.toInput(principal.auth(), offerId);

        BuyOfferOutput output = useCase.execute(input);

        BuyStoreOfferResponse dto = BuyStoreOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
