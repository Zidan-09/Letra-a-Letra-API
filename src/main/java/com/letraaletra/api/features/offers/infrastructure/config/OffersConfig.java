package com.letraaletra.api.features.offers.infrastructure.config;

import com.letraaletra.api.features.offers.application.usecase.*;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OffersConfig {
    @Bean
    public RegisterOfferUseCase registerOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory
    ) {
        return new RegisterOfferUseCase(
                offerRepository,
                adminChecker,
                rewardFactory
        );
    }

    @Bean
    public EnableOfferUseCase enableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        return new EnableOfferUseCase(
                offerRepository,
                adminChecker
        );
    }

    @Bean
    public DisableOfferUseCase disableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        return new DisableOfferUseCase(
                offerRepository,
                adminChecker
        );
    }

    @Bean
    public FindOfferUseCase findOfferUseCase(
            OfferRepository offerRepository
    ) {
        return new FindOfferUseCase(
                offerRepository
        );
    }

    @Bean
    public GetOffersUseCase getOffersUseCase(
            OfferRepository offerRepository
    ) {
        return new GetOffersUseCase(
                offerRepository
        );
    }

    @Bean
    public DeleteOfferUseCase deleteOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker
    ) {
        return new DeleteOfferUseCase(
                offerRepository,
                adminChecker
        );
    }
}
