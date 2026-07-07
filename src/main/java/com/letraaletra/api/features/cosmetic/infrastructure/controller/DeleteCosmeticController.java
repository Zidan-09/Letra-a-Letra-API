package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.DeleteCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DeleteCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DeleteCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class DeleteCosmeticController {
    private final DeleteCosmeticUseCase useCase;

    public DeleteCosmeticController(
            DeleteCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @DeleteMapping(path = "/{cosmeticId}")
    public ResponseEntity<SuccessResponse<DeleteCosmeticResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @PathVariable @NotBlank String cosmeticId
    ) {
        DeleteCosmeticInput input = DeleteCosmeticMapper.toInput(auth, cosmeticId);

        DeleteCosmeticOutput output = useCase.execute(input);

        DeleteCosmeticResponse dto = DeleteCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
