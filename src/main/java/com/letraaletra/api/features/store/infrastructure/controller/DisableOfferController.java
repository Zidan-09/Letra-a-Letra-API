package com.letraaletra.api.features.store.infrastructure.controller;

import com.letraaletra.api.features.store.application.input.DisableOfferInput;
import com.letraaletra.api.features.store.application.output.DisableOfferOutput;
import com.letraaletra.api.features.store.application.usecase.DisableOfferUseCase;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.DisableOfferResponse;
import com.letraaletra.api.features.store.infrastructure.presentation.mapper.DisableOfferMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DisableOfferController {
    private final DisableOfferUseCase useCase;

    public DisableOfferController(DisableOfferUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/store/{offerId}")
    public ResponseEntity<SuccessResponse<DisableOfferResponse>> disableOffer(
            @AuthenticationPrincipal User user,
            @PathVariable @NotBlank String offerId
    ) {
        DisableOfferInput input = DisableOfferMapper.toInput(user, offerId);

        DisableOfferOutput output = useCase.execute(input);

        DisableOfferResponse dto = DisableOfferMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
