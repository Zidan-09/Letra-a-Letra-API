package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GetCosmeticsMapper {
    public static GetCosmeticsInput toInput(Pageable pageable) {

        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetCosmeticsInput(
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<CosmeticDTO> toResponse(GetCosmeticsOutput output) {
        Page<Cosmetic> page = output.cosmetics();

        return new PageResponse<>(
                page.getContent()
                        .stream().map(CosmeticMapper::toDto)
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
