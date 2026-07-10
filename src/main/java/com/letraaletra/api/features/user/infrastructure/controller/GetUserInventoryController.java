package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetUserInventoryResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetUserInventoryMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetUserInventoryController {
    private final UseCase<GetUserInventoryInput, GetUserInventoryOutput> useCase;

    public GetUserInventoryController(
            UseCase<GetUserInventoryInput, GetUserInventoryOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/inventory")
    public ResponseEntity<SuccessResponse<GetUserInventoryResponse>> getUserInventory(
            @AuthenticationPrincipal UUID auth
        ) {
        GetUserInventoryInput input = GetUserInventoryMapper.toInput(auth);

        GetUserInventoryOutput output = useCase.execute(input);

        GetUserInventoryResponse dto = GetUserInventoryMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
