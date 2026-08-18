package com.letraaletra.api.features.offers.application.output;

import com.letraaletra.api.features.offers.domain.Offer;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetOffersOutput(
        Page<Offer> offers
) {
}
