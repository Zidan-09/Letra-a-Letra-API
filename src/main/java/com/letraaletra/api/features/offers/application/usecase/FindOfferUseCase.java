package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindOfferUseCase implements UseCase<FindOfferInput, FindOfferOutput> {
    private final OfferRepository offerRepository;

    public FindOfferUseCase(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Override
    public FindOfferOutput execute(FindOfferInput input) {
        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        return buildOutput(offer);
    }

    private FindOfferOutput buildOutput(Offer offer) {
        return new FindOfferOutput(
                offer
        );
    }
}
