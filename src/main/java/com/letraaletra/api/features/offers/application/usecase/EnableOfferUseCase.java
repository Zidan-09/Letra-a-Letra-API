package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

public class EnableOfferUseCase implements UseCase<EnableOfferInput, EnableOfferOutput> {
    private final OfferRepository offerRepository;
    private final AdminChecker adminChecker;

    public EnableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        this.offerRepository = offerRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    @Transactional
    public EnableOfferOutput execute(EnableOfferInput input) {
        adminChecker.check(input.principal(), PermissionKey.OFFERS, PermissionAction.TOGGLE);

        Offer offer = offerRepository.findById(input.offerId())
                .orElseThrow(OfferNotFoundException::new);

        offer.enable();

        offerRepository.save(offer);

        return buildOutput(offer);
    }

    private EnableOfferOutput buildOutput(Offer offer) {
        return new EnableOfferOutput(
                offer
        );
    }
}
