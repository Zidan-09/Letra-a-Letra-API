package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.offers.domain.rewards.CosmeticReward;
import com.letraaletra.api.features.offers.domain.rewards.HardGemsReward;
import com.letraaletra.api.features.offers.domain.rewards.SoftCoinsReward;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RegisterOfferUseCase implements UseCase<RegisterOfferInput, RegisterOfferOutput> {
    private final OfferRepository offerRepository;
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public RegisterOfferUseCase(
            OfferRepository offerRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.offerRepository = offerRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public RegisterOfferOutput execute(RegisterOfferInput input) {
        adminChecker.check(input.auth());

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
                true,
                LocalDateTime.now().plusHours(input.expiresIn())
        );
    }

    private List<OfferReward> buildRewards(List<RegisterOfferRewardInput> rewardRequests) {
        return rewardRequests.stream()
                .map(this::buildReward)
                .toList();
    }

    private OfferReward buildReward(RegisterOfferRewardInput reward) {
        UUID id = UUID.randomUUID();

        return switch (reward.rewardType()) {
            case COIN -> new OfferReward(
                    id,
                    new SoftCoinsReward(reward.quantity())
            );

            case GEMS -> new OfferReward(
                    id,
                    new HardGemsReward(reward.quantity())
            );

            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticRepository.find(reward.rewardReference())
                        .orElseThrow(CosmeticNotFoundException::new);

                yield new OfferReward(
                        id,
                        new CosmeticReward(cosmetic)
                );
            }
        };
    }
}
