package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.FindOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.FindOfferMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/offer")
@Tag(name = "Offer", description = "Rotas relacionadas ao gerenciamento de ofertas da loja")
public class FindOfferController {
    private final UseCase<FindOfferInput, FindOfferOutput> useCase;

    public FindOfferController(
            UseCase<FindOfferInput, FindOfferOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/{offerId}")
    public ResponseEntity<SuccessResponse<FindOfferResponse>> handle(
            @PathVariable UUID offerId
    ) {
        FindOfferInput input = FindOfferMapper.toInput(offerId);

        FindOfferOutput output = useCase.execute(input);

        FindOfferResponse dto = FindOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
