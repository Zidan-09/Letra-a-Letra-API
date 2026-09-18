package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.CreateItemMapper;
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
@Tag(name = "Items", description = "Rotas do catálogo de itens")
public class CreateItemController {
    private final UseCase<CreateItemInput, CreateItemOutput> useCase;

    @PostMapping(path = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ItemResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestPart("item") CreateItemRequest request,
            @RequestPart(value = "asset", required = false) MultipartFile asset
    ) {
        CreateItemInput input = CreateItemMapper.toInput(principal, request, asset);

        CreateItemOutput output = useCase.execute(input);

        ItemResponse dto = CreateItemMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
