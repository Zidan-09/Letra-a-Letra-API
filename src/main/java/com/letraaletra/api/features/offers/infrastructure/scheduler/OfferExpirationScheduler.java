package com.letraaletra.api.features.offers.infrastructure.scheduler;

import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferExpirationScheduler {
    private final OfferRepository offerRepository;

    @Scheduled(fixedRate = 60000)
    public void expireOffers() {
        offerRepository.expireOffers();
    }
}
