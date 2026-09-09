package com.letraaletra.api.features.offers.infrastructure.config;

import com.letraaletra.api.features.offers.application.input.DeleteOfferInput;
import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.output.DeleteOfferOutput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.application.usecase.*;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OffersConfig {
    @Bean
    public UseCase<RegisterOfferInput, RegisterOfferOutput> registerOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RegisterOfferUseCase(
                        offerRepository,
                        adminChecker,
                        rewardFactory
                ),
                transactions
        );
    }

    @Bean
    public UseCase<EnableOfferInput, EnableOfferOutput> enableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new EnableOfferUseCase(
                        offerRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DisableOfferInput, DisableOfferOutput> disableOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DisableOfferUseCase(
                        offerRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindOfferInput, FindOfferOutput> findOfferUseCase(
            OfferRepository offerRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindOfferUseCase(
                        offerRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetOffersInput, GetOffersOutput> getOffersUseCase(
            OfferRepository offerRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetOffersUseCase(
                        offerRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DeleteOfferInput, DeleteOfferOutput> deleteOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DeleteOfferUseCase(
                        offerRepository,
                        adminChecker
                ),
                transactions
        );
    }
}
