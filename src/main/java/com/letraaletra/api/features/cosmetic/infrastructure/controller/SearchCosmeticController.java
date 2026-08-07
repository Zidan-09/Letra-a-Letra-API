package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.SearchCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.SearchCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.SearchCosmeticMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class SearchCosmeticController {
    private final UseCase<SearchCosmeticInput, SearchCosmeticOutput> useCase;

    @GetMapping(path = "/search")
    public ResponseEntity<SuccessResponse<PageResponse<CosmeticResponse>>> handle(
            @RequestParam String search,
            Pageable pageable
    ) {
        SearchCosmeticInput input = SearchCosmeticMapper.toInput(search, pageable);

        SearchCosmeticOutput output = useCase.execute(input);

        PageResponse<CosmeticResponse> dto = SearchCosmeticMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
