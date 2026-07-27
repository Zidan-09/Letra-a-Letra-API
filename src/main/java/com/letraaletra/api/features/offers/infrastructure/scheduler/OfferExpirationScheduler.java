package com.letraaletra.api.features.offers.infrastructure.scheduler;

import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OfferExpirationScheduler {
    private final OfferRepository offerRepository;

    public OfferExpirationScheduler(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void expireOffers() {
        offerRepository.expireOffers();
    }
}
