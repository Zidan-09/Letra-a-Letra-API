package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetActiveOffersUseCase implements UseCase<Void, GetActiveOffersOutput> {
    private final OfferRepository offerRepository;

    public GetActiveOffersUseCase(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Override
    public GetActiveOffersOutput execute(Void input) {
        List<Offer> activeOffers = offerRepository.getActiveOffers();

        return buildOutput(activeOffers);
    }

    private GetActiveOffersOutput buildOutput(List<Offer> offers) {
        return new GetActiveOffersOutput(offers);
    }
}
