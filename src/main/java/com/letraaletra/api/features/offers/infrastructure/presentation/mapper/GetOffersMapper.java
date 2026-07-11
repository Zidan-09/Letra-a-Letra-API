package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.GetOffersResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GetOffersMapper {
    public static GetOffersInput toInput(Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetOffersInput(
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static GetOffersResponse toResponse(GetOffersOutput output) {
        return new GetOffersResponse(
                output.offers()
        );
    }
}
