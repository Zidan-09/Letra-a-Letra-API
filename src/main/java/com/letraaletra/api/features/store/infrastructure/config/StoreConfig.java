package com.letraaletra.api.features.store.infrastructure.config;

import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.store.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.store.application.usecase.DisableOfferUseCase;
import com.letraaletra.api.features.store.application.usecase.GetActiveOffersUseCase;
import com.letraaletra.api.features.store.application.usecase.RegisterOfferUseCase;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean
    public RegisterOfferUseCase registerOfferUseCase(
            StoreOfferRepository storeOfferRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        return new RegisterOfferUseCase(
                storeOfferRepository,
                cosmeticRepository,
                adminChecker
        );
    }

    @Bean
    public DisableOfferUseCase disableOfferUseCase(
            StoreOfferRepository storeOfferRepository,
            AdminChecker adminChecker
    ) {
        return new DisableOfferUseCase(
                storeOfferRepository,
                adminChecker
        );
    }

    @Bean
    public GetActiveOffersUseCase getActiveOffersUseCase(
            StoreOfferRepository storeOfferRepository
    ) {
        return new GetActiveOffersUseCase(
                storeOfferRepository
        );
    }

    @Bean
    public BuyOfferUseCase buyOfferUseCase(
            UserRepository userRepository,
            StoreOfferRepository storeOfferRepository
    ) {
        return new BuyOfferUseCase(
                userRepository,
                storeOfferRepository
        );
    }
}
