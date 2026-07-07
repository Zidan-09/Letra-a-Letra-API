package com.letraaletra.api.features.store.infrastructure.config;

import com.letraaletra.api.features.store.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.store.application.usecase.GetActiveOffersUseCase;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean
    public GetActiveOffersUseCase getActiveOffersUseCase(
            OfferRepository offerRepository
    ) {
        return new GetActiveOffersUseCase(
                offerRepository
        );
    }

    @Bean
    public BuyOfferUseCase buyOfferUseCase(
            UserRepository userRepository,
            OfferRepository offerRepository
    ) {
        return new BuyOfferUseCase(
                userRepository,
                offerRepository
        );
    }
}
