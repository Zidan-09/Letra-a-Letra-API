package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DisableCosmeticUseCase implements UseCase<DisableCosmeticInput, DisableCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;

    public DisableCosmeticUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public DisableCosmeticOutput execute(DisableCosmeticInput input) {
        Cosmetic cosmetic = cosmeticRepository.find(input.id())
                .orElseThrow(CosmeticNotFoundException::new);

        cosmetic.setAvailable(false);

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private DisableCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new DisableCosmeticOutput(
                cosmetic
        );
    }
}
