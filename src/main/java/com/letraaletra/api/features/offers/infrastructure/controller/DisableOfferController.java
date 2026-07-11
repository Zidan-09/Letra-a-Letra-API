package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DisableOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.DisableOfferMapper;
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
public class DisableOfferController {
    private final UseCase<DisableOfferInput, DisableOfferOutput> useCase;

    public DisableOfferController(
            UseCase<DisableOfferInput, DisableOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/disable/{offerId}")
    public ResponseEntity<SuccessResponse<DisableOfferResponse>> disableOffer(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID offerId
    ) {
        DisableOfferInput input = DisableOfferMapper.toInput(principal.auth(), offerId);

        DisableOfferOutput output = useCase.execute(input);

        DisableOfferResponse dto = DisableOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
