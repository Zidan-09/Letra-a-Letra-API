package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.store.application.input.RegisterOfferInput;
import com.letraaletra.api.features.store.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterOfferUseCase implements UseCase<RegisterOfferInput, RegisterOfferOutput> {
    private final StoreOfferRepository storeOfferRepository;
    private final CosmeticRepository cosmeticRepository;

    public RegisterOfferUseCase(
            StoreOfferRepository storeOfferRepository,
            CosmeticRepository cosmeticRepository
    ) {
        this.storeOfferRepository = storeOfferRepository;
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public RegisterOfferOutput execute(RegisterOfferInput input) {
        Cosmetic cosmetic = cosmeticRepository.find(input.cosmeticId()).orElseThrow(CosmeticNotFoundException::new);

        StoreOffer storeOffer = new StoreOffer(
                UUID.randomUUID().toString(),
                input.title(),
                input.coinType(),
                input.price(),
                cosmetic,
                input.rewardSoftCoins(),
                input.rewardHardGems(),
                true,
                LocalDateTime.now()
        );

        storeOfferRepository.save(storeOffer);

        return buildOutput(storeOffer);
    }

    private RegisterOfferOutput buildOutput(StoreOffer storeOffer) {
        return new RegisterOfferOutput(
                storeOffer
        );
    }
}
