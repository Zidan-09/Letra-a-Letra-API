package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

public class EnableCosmeticUseCase implements UseCase<EnableCosmeticInput, EnableCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public EnableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    @Transactional
    public EnableCosmeticOutput execute(EnableCosmeticInput input) {
        adminChecker.check(input.principal());

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
