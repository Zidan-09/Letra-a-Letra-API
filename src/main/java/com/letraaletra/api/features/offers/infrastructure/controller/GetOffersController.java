package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.GetOffersResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.GetOffersMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/offer")
@Tag(name = "Offer", description = "Rotas relacionadas ao gerenciamento de ofertas da loja")
public class GetOffersController {
    private final UseCase<GetOffersInput, GetOffersOutput> useCase;

    public GetOffersController(
            UseCase<GetOffersInput, GetOffersOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<GetOffersResponse>> handle(
            Pageable pageable
    ) {
        GetOffersInput input = GetOffersMapper.toInput(pageable);

        GetOffersOutput output = useCase.execute(input);

        GetOffersResponse dto = GetOffersMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
