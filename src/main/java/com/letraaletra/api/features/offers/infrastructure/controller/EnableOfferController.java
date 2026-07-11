package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.EnableOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.EnableOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/offer")
@Tag(name = "Offer", description = "Rotas relacionadas ao gerenciamento de ofertas da loja")
public class EnableOfferController {
    private final UseCase<EnableOfferInput, EnableOfferOutput> useCase;

    public EnableOfferController(
            UseCase<EnableOfferInput, EnableOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/enable/{offerId}")
    public ResponseEntity<SuccessResponse<EnableOfferResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID offerId
    ) {
        EnableOfferInput input = EnableOfferMapper.toInput(principal.auth(), offerId);

        EnableOfferOutput output = useCase.execute(input);

        EnableOfferResponse dto = EnableOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
