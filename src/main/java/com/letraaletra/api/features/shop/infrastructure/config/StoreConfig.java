package com.letraaletra.api.features.shop.infrastructure.config;

import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.features.shop.application.usecase.BuyOfferUseCase;
import com.letraaletra.api.features.shop.application.usecase.GetActiveOffersUseCase;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean
    public UseCase<Void, GetActiveOffersOutput> getActiveOffersUseCase(
            OfferRepository offerRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetActiveOffersUseCase(
                        offerRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<BuyOfferInput, BuyOfferOutput> buyOfferUseCase(
            ShopPurchasePort purchasePort,
            OfferRepository offerRepository,
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new BuyOfferUseCase(
                        purchasePort,
                        offerRepository,
                        userRepository,
                        auditRecorder
                ),
                transactions
        );
    }
}
