package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.store.application.input.RegisterOfferInput;
import com.letraaletra.api.features.store.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;

public class RegisterOfferUseCase implements UseCase<RegisterOfferInput, RegisterOfferOutput> {
    private final StoreOfferRepository storeOfferRepository;
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public RegisterOfferUseCase(
            StoreOfferRepository storeOfferRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.storeOfferRepository = storeOfferRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public RegisterOfferOutput execute(RegisterOfferInput input) {
        adminChecker.check(input.auth());

        Cosmetic cosmetic = cosmeticRepository.find(input.cosmeticId()).orElseThrow(CosmeticNotFoundException::new);

        StoreOffer storeOffer = buildOffer(input, cosmetic);

        storeOfferRepository.save(storeOffer);

        return buildOutput(storeOffer);
    }

    private StoreOffer buildOffer(RegisterOfferInput input, Cosmetic cosmetic) {
        return StoreOffer.create(
                input.title(),
                input.coinType(),
                input.price(),
                cosmetic,
                input.rewardSoftCoins(),
                input.rewardHardGems(),
                true,
                LocalDateTime.now().plusHours(input.expiresIn())
        );
    }

    private RegisterOfferOutput buildOutput(StoreOffer storeOffer) {
        return new RegisterOfferOutput(
                storeOffer
        );
    }
}
