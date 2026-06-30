package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.GetCosmeticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GetCosmeticsMapper {
    public static GetCosmeticsInput toInput(Pageable pageable) {
        return new GetCosmeticsInput(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static GetCosmeticsResponse toResponse(GetCosmeticsOutput output) {
        Page<Cosmetic> page = output.cosmetics();

        return new GetCosmeticsResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
