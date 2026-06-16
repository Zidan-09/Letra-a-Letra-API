package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.input.DisableOfferInput;
import com.letraaletra.api.features.store.application.output.DisableOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DisableOfferUseCase implements UseCase<DisableOfferInput, DisableOfferOutput> {
    private final StoreOfferRepository storeOfferRepository;

    public DisableOfferUseCase(
            StoreOfferRepository storeOfferRepository
    ) {
        this.storeOfferRepository = storeOfferRepository;
    }

    @Override
    public DisableOfferOutput execute(DisableOfferInput input) {
        StoreOffer offer = storeOfferRepository.findById(input.offerId());

        offer.setActive(false);

        storeOfferRepository.save(offer);

        return buildOutput(offer);
    }

    private DisableOfferOutput buildOutput(StoreOffer offer) {
        return new DisableOfferOutput(offer);
    }
}
