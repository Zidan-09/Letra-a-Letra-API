package com.letraaletra.api.features.store.infrastructure.presentation.mapper;

import com.letraaletra.api.features.store.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.store.infrastructure.presentation.dto.response.GetActiveOffersResponse;

public class GetActiveOffersMapper {
    public static GetActiveOffersResponse toResponse(GetActiveOffersOutput output) {
        return new GetActiveOffersResponse(output.offers());
    }
}
