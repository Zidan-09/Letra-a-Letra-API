package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class RegisterOfferUseCase implements UseCase<RegisterOfferInput, RegisterOfferOutput> {
    private final OfferRepository offerRepository;
    private final AdminChecker adminChecker;
    private final RewardFactory rewardFactory;

    public RegisterOfferUseCase(
            OfferRepository offerRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory
    ) {
        this.offerRepository = offerRepository;
        this.adminChecker = adminChecker;
        this.rewardFactory = rewardFactory;
    }

    @Override
    @Transactional
    public RegisterOfferOutput execute(RegisterOfferInput input) {
        adminChecker.check(input.principal(), PermissionKey.OFFERS, PermissionAction.CREATE);

        Offer offer = buildOffer(input);

        offerRepository.save(offer);

        return new RegisterOfferOutput(offer);
    }

    private Offer buildOffer(RegisterOfferInput input) {
        return Offer.create(
                input.title(),
                input.coinType(),
                input.price(),
                buildRewards(input.rewards()),
                input.repeatable(),
                input.hasExpiration(),
                input.expiresIn()
        );
    }

    private List<OfferReward> buildRewards(List<RegisterOfferRewardInput> rewardRequests) {
        return rewardRequests.stream()
                .map(this::buildReward)
                .toList();
    }

    private OfferReward buildReward(RegisterOfferRewardInput reward) {
        UUID id = UUID.randomUUID();

        return new OfferReward(
                id,
                rewardFactory.create(
                        reward.rewardType(),
                        reward.quantity(),
                        reward.rewardReference()
                )
        );
    }
}
