package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.application.usecase.GetUserInventoryUseCase;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetUserInventoryResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetUserInventoryMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class GetUserInventoryController {
    private final GetUserInventoryUseCase useCase;

    public GetUserInventoryController(
            GetUserInventoryUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/inventory")
    public ResponseEntity<SuccessResponse<GetUserInventoryResponse>> getUserInventory(
            @AuthenticationPrincipal User user
        ) {
        GetUserInventoryInput input = GetUserInventoryMapper.toInput(user.getId().toString());

        GetUserInventoryOutput output = useCase.execute(input);

        GetUserInventoryResponse dto = GetUserInventoryMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
