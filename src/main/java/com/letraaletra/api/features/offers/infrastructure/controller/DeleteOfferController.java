package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.DeleteOfferInput;
import com.letraaletra.api.features.offers.application.output.DeleteOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DeleteOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.DeleteOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/offer")
@Tag(name = "Offer", description = "Rotas relacionadas ao gerenciamento de ofertas da loja")
public class DeleteOfferController {
    private final UseCase<DeleteOfferInput, DeleteOfferOutput> useCase;

    public DeleteOfferController(
            UseCase<DeleteOfferInput, DeleteOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @DeleteMapping(path = "/{offerId}")
    public ResponseEntity<SuccessResponse<DeleteOfferResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID offerId
    ) {
        DeleteOfferInput input = DeleteOfferMapper.toInput(principal.auth(), offerId);

        DeleteOfferOutput output = useCase.execute(input);

        DeleteOfferResponse dto = DeleteOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
