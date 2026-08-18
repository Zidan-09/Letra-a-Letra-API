package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindCosmeticByNameUseCase implements UseCase<FindCosmeticByNameInput, FindCosmeticByNameOutput> {
    private final CosmeticRepository cosmeticRepository;

    public FindCosmeticByNameUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public FindCosmeticByNameOutput execute(FindCosmeticByNameInput input) {
        Cosmetic cosmetic = cosmeticRepository.findByName(input.name())
                .orElseThrow(CosmeticNotFoundException::new);

        return new FindCosmeticByNameOutput(cosmetic);
    }
}
