package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

public class DisableCosmeticUseCase implements UseCase<DisableCosmeticInput, DisableCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public DisableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    @Transactional
    public DisableCosmeticOutput execute(DisableCosmeticInput input) {
        adminChecker.check(input.principal(), PermissionKey.COSMETIC, PermissionAction.TOGGLE);

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
