package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.UpdateItemDefinitionRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.UpdateItemDefinitionMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class UpdateItemDefinitionController {
    private final UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> useCase;

    @PutMapping(path = "/items/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ItemDefinitionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID itemId,
            @Valid @RequestPart("item") UpdateItemDefinitionRequest request,
            @RequestPart(value = "asset", required = false) MultipartFile asset
    ) {
        UpdateItemDefinitionInput input = UpdateItemDefinitionMapper.toInput(principal, itemId, request, asset);

        UpdateItemDefinitionOutput output = useCase.execute(input);

        ItemDefinitionResponse dto = UpdateItemDefinitionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
