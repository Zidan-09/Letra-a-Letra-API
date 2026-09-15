package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.GetUserItemsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.GetUserItemsMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "Inventory", description = "Rotas do inventário genérico de itens do usuário")
public class GetUserItemsByIdController {
    private final UseCase<GetUserItemsInput, GetUserItemsOutput> useCase;

    @GetMapping(path = "/{userId}/items")
    public ResponseEntity<SuccessResponse<GetUserItemsResponse>> handle(
            @PathVariable UUID userId,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) Boolean equipped
    ) {
        GetUserItemsInput input = GetUserItemsMapper.toInput(
                userId, kind, category, context, equipped);

        GetUserItemsOutput output = useCase.execute(input);

        GetUserItemsResponse dto = GetUserItemsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
