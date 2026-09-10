package com.letraaletra.api.features.cosmetic.infrastructure.config;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.input.SearchCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.output.SearchCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.application.usecase.*;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmeticConfig {
    @Bean
    public UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> setAvatarUseCase(
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ChangeCosmeticUseCase(userRepository, auditRecorder),
                transactions
        );
    }

    @Bean
    public UseCase<RegisterCosmeticInput, RegisterCosmeticOutput> registerCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RegisterCosmeticUseCase(
                        cosmeticRepository,
                        storageGateway,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UpdateCosmeticInput, UpdateCosmeticOutput> updateCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway storageGateway,
            ImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UpdateCosmeticUseCase(
                        cosmeticRepository,
                        storageGateway,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<EnableCosmeticInput, EnableCosmeticOutput> enableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new EnableCosmeticUseCase(
                        cosmeticRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DisableCosmeticInput, DisableCosmeticOutput> disableCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DisableCosmeticUseCase(
                        cosmeticRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetCosmeticsInput, GetCosmeticsOutput> getCosmeticsUseCase(
            CosmeticRepository cosmeticRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetCosmeticsUseCase(
                        cosmeticRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DeleteCosmeticInput, DeleteCosmeticOutput> deleteCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            AssetStorageGateway assetStorageGateway,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DeleteCosmeticUseCase(
                        cosmeticRepository,
                        assetStorageGateway,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindCosmeticByNameInput, FindCosmeticByNameOutput> findCosmeticByNameUseCase(
            CosmeticRepository cosmeticRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindCosmeticByNameUseCase(
                        cosmeticRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<SearchCosmeticInput, SearchCosmeticOutput> searchCosmeticUseCase(
            CosmeticRepository cosmeticRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new SearchCosmeticUseCase(
                        cosmeticRepository
                ),
                transactions
        );
    }
}
