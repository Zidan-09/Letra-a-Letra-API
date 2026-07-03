package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class EnableCosmeticUseCase implements UseCase<EnableCosmeticInput, EnableCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;

    public EnableCosmeticUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public EnableCosmeticOutput execute(EnableCosmeticInput input) {
        Cosmetic cosmetic = cosmeticRepository.find(input.id())
                .orElseThrow(CosmeticNotFoundException::new);

        cosmetic.setAvailable(true);

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private EnableCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new EnableCosmeticOutput(
                cosmetic
        );
    }
}
