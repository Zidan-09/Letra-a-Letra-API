package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetOffersUseCase implements UseCase<Void, GetOffersOutput> {
    private final OfferRepository offerRepository;

    public GetOffersUseCase(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Override
    public GetOffersOutput execute(Void unused) {
        List<Offer> offers = offerRepository.get();

        return buildOutput(offers);
    }

    private GetOffersOutput buildOutput(List<Offer> offers) {
        return new GetOffersOutput(
                offers
        );
    }
}
