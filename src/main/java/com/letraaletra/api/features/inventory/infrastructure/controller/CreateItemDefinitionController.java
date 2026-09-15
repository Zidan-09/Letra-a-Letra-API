package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.CreateItemDefinitionRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.CreateItemDefinitionMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Inventory", description = "Rotas do inventário genérico de itens do usuário")
public class CreateItemDefinitionController {
    private final UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> useCase;

    @PostMapping(path = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ItemDefinitionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestPart("item") CreateItemDefinitionRequest request,
            @RequestPart(value = "asset", required = false) MultipartFile asset
    ) {
        CreateItemDefinitionInput input = CreateItemDefinitionMapper.toInput(principal, request, asset);

        CreateItemDefinitionOutput output = useCase.execute(input);

        ItemDefinitionResponse dto = CreateItemDefinitionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
