package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RegisterCosmeticUseCase implements UseCase<RegisterCosmeticInput, RegisterCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;

    public RegisterCosmeticUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public RegisterCosmeticOutput execute(RegisterCosmeticInput input) {
        Cosmetic exists = cosmeticRepository.find(input.id()).orElse(null);
        validateIfExists(exists);

        Cosmetic cosmetic = buildCosmetic(input);

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private RegisterCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new RegisterCosmeticOutput(
            cosmetic
        );
    }

    private Cosmetic buildCosmetic(RegisterCosmeticInput input) {
        return new Cosmetic(
                input.id(),
                input.name(),
                input.type()
        );
    }

    private void validateIfExists(Cosmetic exists) {
        if (exists != null) {
            throw new RuntimeException("cosmetic_already_exists");
        }
    }
}
