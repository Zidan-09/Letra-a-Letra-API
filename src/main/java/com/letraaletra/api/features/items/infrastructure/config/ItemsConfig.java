package com.letraaletra.api.features.items.infrastructure.config;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.input.DeleteItemInput;
import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.input.ListItemsInput;
import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.input.UpdateItemInput;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.application.output.DeleteItemOutput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
import com.letraaletra.api.features.items.application.output.ListItemsOutput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.application.output.UpdateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.application.usecase.CreateItemUseCase;
import com.letraaletra.api.features.items.application.usecase.DeleteItemUseCase;
import com.letraaletra.api.features.items.application.usecase.GetItemUseCase;
import com.letraaletra.api.features.items.application.usecase.ListItemsUseCase;
import com.letraaletra.api.features.items.application.usecase.ToggleItemAvailabilityUseCase;
import com.letraaletra.api.features.items.application.usecase.UpdateItemUseCase;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemsConfig {
    @Bean
    public UseCase<CreateItemInput, CreateItemOutput> createItemUseCase(
            ItemRepository itemRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateItemUseCase(
                        itemRepository,
                        assetStorage,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UpdateItemInput, UpdateItemOutput> updateItemUseCase(
            ItemRepository itemRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UpdateItemUseCase(
                        itemRepository,
                        assetStorage,
                        imageConverter,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetItemInput, GetItemOutput> getItemUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetItemUseCase(
                        itemRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ListItemsInput, ListItemsOutput> listItemsUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ListItemsUseCase(
                        itemRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DeleteItemInput, DeleteItemOutput> deleteItemUseCase(
            ItemRepository itemRepository,
            ItemAssetStorage assetStorage,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DeleteItemUseCase(
                        itemRepository,
                        assetStorage,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ToggleItemAvailabilityInput, ToggleItemAvailabilityOutput> toggleItemAvailabilityUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ToggleItemAvailabilityUseCase(
                        itemRepository,
                        adminChecker
                ),
                transactions
        );
    }
}
