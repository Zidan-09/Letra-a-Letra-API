package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.GetOffersResponse;

public class GetOffersMapper {
    public static GetOffersResponse toResponse(GetOffersOutput output) {
        return new GetOffersResponse(
                output.offers()
        );
    }
}
