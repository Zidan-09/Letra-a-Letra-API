package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.GetOffersResponse;
import org.springframework.data.domain.Pageable;

public class GetOffersMapper {
    public static GetOffersInput toInput(Pageable pageable) {
        return new GetOffersInput(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static GetOffersResponse toResponse(GetOffersOutput output) {
        return new GetOffersResponse(
                output.offers()
        );
    }
}
