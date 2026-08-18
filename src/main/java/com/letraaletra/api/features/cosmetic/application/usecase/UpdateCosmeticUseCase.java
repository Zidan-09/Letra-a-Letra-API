package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.UUID;

public class UpdateCosmeticUseCase implements UseCase<UpdateCosmeticInput, UpdateCosmeticOutput> {

    private final CosmeticRepository cosmeticRepository;
    private final ImageConverter imageConverter;
    private final AssetStorageGateway storageGateway;
    private final AdminChecker adminChecker;

    public UpdateCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter,
            AdminChecker adminChecker
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.storageGateway = storageGateway;
        this.imageConverter = imageConverter;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateCosmeticOutput execute(UpdateCosmeticInput input) {
        adminChecker.check(input.principal(), PermissionKey.COSMETIC, PermissionAction.EDIT);

        Cosmetic cosmetic = checkCosmetic(input.id(), input.name());

        String oldAssetPath = cosmetic.getAssetPath();
        String newAssetPath = null;
        boolean replaceOldAsset = false;

        if (input.isNewAsset()) {
            newAssetPath = saveNewAsset(input);
            cosmetic.setAssetPath(newAssetPath);
            replaceOldAsset = true;

        } else if (shouldMoveAsset(input, cosmetic)) {
            newAssetPath = copyAsset(input, cosmetic);
            cosmetic.setAssetPath(newAssetPath);
            replaceOldAsset = true;
        }

        cosmetic.setName(input.name());
        cosmetic.setType(input.type());
        cosmetic.incrementVersion();

        try {
            cosmeticRepository.save(cosmetic);

        } catch (Exception e) {
            if (replaceOldAsset) {
                storageGateway.delete(newAssetPath);
            }
            throw e;
        }

        if (replaceOldAsset) {
            storageGateway.delete(oldAssetPath);
        }

        return new UpdateCosmeticOutput(cosmetic);
    }

    private Cosmetic checkCosmetic(UUID id, String name) {
        Cosmetic cosmetic = cosmeticRepository.find(id)
                .orElseThrow(CosmeticNotFoundException::new);

        cosmeticRepository.findByName(name)
                .filter(found ->
                        !found.getId().equals(cosmetic.getId()))
                .ifPresent(found -> {
                    throw new InvalidCosmeticException();
                });

        return cosmetic;
    }

    private boolean shouldMoveAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        return !input.name().equals(cosmetic.getName())
                || !input.type().equals(cosmetic.getType());
    }

    private String copyAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        return storageGateway.copy(
                cosmetic.getAssetPath(),
                input.name(),
                input.type()
        );
    }

    private String saveNewAsset(UpdateCosmeticInput input) {
        byte[] image = imageConverter.convertToWebp(input.asset());

        return storageGateway.upload(
                image,
                input.name(),
                input.type()
        );
    }
}