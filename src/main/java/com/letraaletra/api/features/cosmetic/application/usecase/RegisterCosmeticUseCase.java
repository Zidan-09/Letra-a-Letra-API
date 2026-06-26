package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RegisterCosmeticUseCase implements UseCase<RegisterCosmeticInput, RegisterCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;
    private final ImageConverter imageConverter;
    private final AssetStorageGateway storageGateway;

    public RegisterCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter
    ) {
        this.cosmeticRepository = cosmeticRepository;
        this.storageGateway = storageGateway;
        this.imageConverter = imageConverter;
    }

    @Override
    public RegisterCosmeticOutput execute(RegisterCosmeticInput input) {
        Cosmetic exists = cosmeticRepository.find(input.id()).orElse(null);
        validateIfExists(exists);

        byte[] image = imageConverter.convertToWebp(input.asset());

        String assetPath = storageGateway.upload(image, input.name(), input.type());

        Cosmetic cosmetic = buildCosmetic(input, assetPath);

        cosmeticRepository.save(cosmetic);

        return buildOutput(cosmetic);
    }

    private RegisterCosmeticOutput buildOutput(Cosmetic cosmetic) {
        return new RegisterCosmeticOutput(
            cosmetic
        );
    }

    private Cosmetic buildCosmetic(RegisterCosmeticInput input, String assetPath) {
        return new Cosmetic(
                input.id(),
                input.name(),
                input.type(),
                assetPath,
                1
        );
    }

    private void validateIfExists(Cosmetic exists) {
        if (exists != null) {
            throw new RuntimeException("cosmetic_already_exists");
        }
    }
}
