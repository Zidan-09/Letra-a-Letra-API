package com.letraaletra.api.features.items.infrastructure.config;

import com.letraaletra.api.features.items.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.items.application.input.DeleteItemDefinitionInput;
import com.letraaletra.api.features.items.application.input.GetItemDefinitionInput;
import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.output.DeleteItemDefinitionOutput;
import com.letraaletra.api.features.items.application.output.GetItemDefinitionOutput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.application.usecase.CreateItemDefinitionUseCase;
import com.letraaletra.api.features.items.application.usecase.DeleteItemDefinitionUseCase;
import com.letraaletra.api.features.items.application.usecase.GetItemDefinitionUseCase;
import com.letraaletra.api.features.items.application.usecase.ListItemDefinitionsUseCase;
import com.letraaletra.api.features.items.application.usecase.UpdateItemDefinitionUseCase;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemsConfig {
    @Bean
    public UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> createItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateItemDefinitionUseCase(
                        itemDefinitionRepository,
                        assetStorage,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> updateItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UpdateItemDefinitionUseCase(
                        itemDefinitionRepository,
                        assetStorage,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetItemDefinitionInput, GetItemDefinitionOutput> getItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetItemDefinitionUseCase(
                        itemDefinitionRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ListItemDefinitionsInput, ListItemDefinitionsOutput> listItemDefinitionsUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ListItemDefinitionsUseCase(
                        itemDefinitionRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DeleteItemDefinitionInput, DeleteItemDefinitionOutput> deleteItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            ItemAssetStorage assetStorage,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DeleteItemDefinitionUseCase(
                        itemDefinitionRepository,
                        assetStorage,
                        adminChecker
                ),
                transactions
        );
    }
}
