package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.DeleteOfferInput;
import com.letraaletra.api.features.offers.application.output.DeleteOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DeleteOfferUseCase implements UseCase<DeleteOfferInput, DeleteOfferOutput> {
    private final OfferRepository offerRepository;
    private final AdminChecker adminChecker;

    public DeleteOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        this.offerRepository = offerRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public DeleteOfferOutput execute(DeleteOfferInput input) {
        adminChecker.check(input.auth());

        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        offerRepository.delete(offer);

        return buildOutput(offer);
    }

    private DeleteOfferOutput buildOutput(Offer offer) {
        return new DeleteOfferOutput(
                offer
        );
    }
}
