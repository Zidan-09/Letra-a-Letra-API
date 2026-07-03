package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class UpdateCosmeticUseCase implements UseCase<UpdateCosmeticInput, UpdateCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final ImageConverter imageConverter;
    private final AssetStorageGateway storageGateway;

    public UpdateCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway assetStorageGateway,
            ImageConverter imageConverter
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.storageGateway = assetStorageGateway;
        this.imageConverter = imageConverter;
    }

    @Override
    public UpdateCosmeticOutput execute(UpdateCosmeticInput input) {
        Cosmetic cosmetic = cosmeticRepository.find(input.id())
                .orElseThrow(CosmeticNotFoundException::new);

        resolveAsset(input, cosmetic);

        cosmetic.setName(input.name());
        cosmetic.setType(input.type());
        cosmetic.incrementVersion();

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private UpdateCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new UpdateCosmeticOutput(
                cosmetic
        );
    }

    private void resolveAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        if (input.isNewAsset()) {
            cosmetic.setAssetPath(saveNewAsset(input, cosmetic));
            return;
        }

        if (shouldMoveAsset(input, cosmetic)) {
            cosmetic.setAssetPath(moveAsset(input, cosmetic));
        }
    }

    private boolean shouldMoveAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        return
            !input.name().equals(cosmetic.getName()) ||
            !input.type().equals(cosmetic.getType());
    }

    private String moveAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        return storageGateway.move(
                cosmetic.getAssetPath(),
                input.name(), input.type()
        );
    }

    private String saveNewAsset(UpdateCosmeticInput input, Cosmetic cosmetic) {
        byte[] image = imageConverter.convertToWebp(input.asset());

        String path = storageGateway.upload(
                image,
                input.name(),
                input.type()
        );

        storageGateway.delete(cosmetic.getAssetPath());

        return path;
    }
}
