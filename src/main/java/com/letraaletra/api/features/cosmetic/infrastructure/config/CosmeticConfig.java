package com.letraaletra.api.features.cosmetic.infrastructure.config;

import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.application.usecase.DisableCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.application.usecase.RegisterCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.application.usecase.UpdateCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmeticConfig {
    @Bean
    public ChangeCosmeticUseCase setAvatarUseCase(UserRepository userRepository) {
        return new ChangeCosmeticUseCase(userRepository);
    }

    @Bean
    public RegisterCosmeticUseCase registerCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter
    ) {
        return new RegisterCosmeticUseCase(
                cosmeticRepository,
                storageGateway,
                imageConverter
        );
    }

    @Bean
    public UpdateCosmeticUseCase updateCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter
    ) {
        return new UpdateCosmeticUseCase(
                cosmeticRepository,
                storageGateway,
                imageConverter
        );
    }

    @Bean
    public DisableCosmeticUseCase deleteCosmeticUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        return new DisableCosmeticUseCase(
                cosmeticRepository
        );
    }
}
