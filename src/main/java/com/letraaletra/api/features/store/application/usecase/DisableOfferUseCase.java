package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.input.DisableOfferInput;
import com.letraaletra.api.features.store.application.output.DisableOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DisableOfferUseCase implements UseCase<DisableOfferInput, DisableOfferOutput> {
    private final StoreOfferRepository storeOfferRepository;
    private final AdminChecker adminChecker;

    public DisableOfferUseCase(
            StoreOfferRepository storeOfferRepository,
            AdminChecker adminChecker
    ) {
        this.storeOfferRepository = storeOfferRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public DisableOfferOutput execute(DisableOfferInput input) {
        adminChecker.check(input.auth());

        StoreOffer offer = storeOfferRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        offer.setActive(false);

        storeOfferRepository.save(offer);

        return buildOutput(offer);
    }

    private DisableOfferOutput buildOutput(StoreOffer offer) {
        return new DisableOfferOutput(offer);
    }
}
