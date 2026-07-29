package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetMyInventoryInput;
import com.letraaletra.api.features.user.application.output.GetMyInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetMyInventoryResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetMyInventoryMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetMyInventoryController {
    private final UseCase<GetMyInventoryInput, GetMyInventoryOutput> useCase;

    public GetMyInventoryController(
            UseCase<GetMyInventoryInput, GetMyInventoryOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/inventory")
    public ResponseEntity<SuccessResponse<GetMyInventoryResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
        ) {
        GetMyInventoryInput input = GetMyInventoryMapper.toInput(principal.auth());

        GetMyInventoryOutput output = useCase.execute(input);

        GetMyInventoryResponse dto = GetMyInventoryMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
