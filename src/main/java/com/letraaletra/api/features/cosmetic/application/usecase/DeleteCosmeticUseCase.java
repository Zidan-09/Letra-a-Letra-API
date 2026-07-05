package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;

public class DeleteCosmeticUseCase implements UseCase<DeleteCosmeticInput, DeleteCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final AssetStorageGateway storageGateway;

    public DeleteCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.storageGateway = storageGateway;
    }

    @Override
    public DeleteCosmeticOutput execute(DeleteCosmeticInput input) {
        validateUser(input.user());

        Cosmetic cosmetic = cosmeticRepository.find(input.cosmeticId())
                .orElseThrow(CosmeticNotFoundException::new);

        storageGateway.delete(cosmetic.getAssetPath());

        cosmeticRepository.delete(input.cosmeticId());

        return buildOutput(cosmetic);
    }

    private void validateUser(User user) {
        if (!user.isAdmin()) {
            throw new UserIsNotAdminException();
        }
    }

    private DeleteCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new DeleteCosmeticOutput(
                cosmetic
        );
    }
}
