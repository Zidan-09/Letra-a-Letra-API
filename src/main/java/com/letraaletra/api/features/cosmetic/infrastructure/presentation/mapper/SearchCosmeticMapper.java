package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.SearchCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.SearchCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticResponse;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class SearchCosmeticMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("name", "type", "available");

    public static SearchCosmeticInput toInput(String search, Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new SearchCosmeticInput(
                search,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<CosmeticResponse> toResponse(SearchCosmeticOutput output) {
        Page<Cosmetic> page = output.result();
        return new PageResponse<>(
                page.getContent()
                        .stream().map(CosmeticResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
