package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DeleteCosmeticUseCase implements UseCase<DeleteCosmeticInput, DeleteCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final AssetStorageGateway storageGateway;
    private final AdminChecker adminChecker;

    public DeleteCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            AdminChecker adminChecker
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.storageGateway = storageGateway;
        this.adminChecker = adminChecker;
    }

    @Override
    public DeleteCosmeticOutput execute(DeleteCosmeticInput input) {
        adminChecker.check(input.auth());

        Cosmetic cosmetic = cosmeticRepository.find(input.cosmeticId())
                .orElseThrow(CosmeticNotFoundException::new);

        storageGateway.delete(cosmetic.getAssetPath());

        cosmeticRepository.delete(input.cosmeticId());

        return buildOutput(cosmetic);
    }

    private DeleteCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new DeleteCosmeticOutput(
                cosmetic
        );
    }
}
