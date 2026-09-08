package com.letraaletra.api.features.shop.infrastructure.config;

import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.features.shop.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.shop.application.usecase.GetActiveOffersUseCase;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
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
            ShopPurchasePort purchasePort,
            OfferRepository offerRepository,
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        return new BuyOfferUseCase(
                purchasePort,
                offerRepository,
                userRepository,
                auditRecorder
        );
    }
}
