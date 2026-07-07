package com.letraaletra.api.features.cosmetic.infrastructure.config;

import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.application.usecase.*;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
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
            ImageConverter imageConverter,
            AdminChecker adminChecker
    ) {
        return new RegisterCosmeticUseCase(
                cosmeticRepository,
                storageGateway,
                imageConverter,
                adminChecker
        );
    }

    @Bean
    public UpdateCosmeticUseCase updateCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter,
            AdminChecker adminChecker
    ) {
        return new UpdateCosmeticUseCase(
                cosmeticRepository,
                storageGateway,
                imageConverter,
                adminChecker
        );
    }

    @Bean
    public EnableCosmeticUseCase enableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        return new EnableCosmeticUseCase(
                cosmeticRepository,
                adminChecker
        );
    }

    @Bean
    public DisableCosmeticUseCase disableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        return new DisableCosmeticUseCase(
                cosmeticRepository,
                adminChecker
        );
    }

    @Bean
    public GetCosmeticsUseCase getCosmeticsUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        return new GetCosmeticsUseCase(
                cosmeticRepository
        );
    }

    @Bean
    public DeleteCosmeticUseCase deleteCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway assetStorageGateway,
            AdminChecker adminChecker
    ) {
        return new DeleteCosmeticUseCase(
                cosmeticRepository,
                assetStorageGateway,
                adminChecker
        );
    }
}
