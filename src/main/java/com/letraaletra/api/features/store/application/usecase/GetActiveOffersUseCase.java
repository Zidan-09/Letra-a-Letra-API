package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCaseWithoutInput;

import java.util.List;

public class GetActiveOffersUseCase implements UseCaseWithoutInput<GetActiveOffersOutput> {
    private final StoreOfferRepository storeOfferRepository;

    public GetActiveOffersUseCase(
            StoreOfferRepository storeOfferRepository
    ) {
        this.storeOfferRepository = storeOfferRepository;
    }

    @Override
    public GetActiveOffersOutput execute() {
        List<StoreOffer> activeOffers = storeOfferRepository.getActiveOffers();

        return buildOutput(activeOffers);
    }

    private GetActiveOffersOutput buildOutput(List<StoreOffer> offers) {
        return new GetActiveOffersOutput(offers);
    }
}
