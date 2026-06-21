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

        byte[] image = imageConverter.convertToWebp(input.asset());

        storageGateway.upload(image, input.name(), cosmetic.getType());

        cosmetic.setName(input.name());
        cosmetic.incrementVersion();

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private UpdateCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new UpdateCosmeticOutput(
                cosmetic
        );
    }
}
