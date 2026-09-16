package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.DeleteItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.DeleteItemDefinitionOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteItemDefinitionUseCase Unit Tests")
class DeleteItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DeleteItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private ItemDefinition cosmeticWithAsset() {
        return ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                ItemContext.PROFILE,
                false,
                null,
                false,
                null,
                "AVATAR/Blue Avatar.webp"
        );
    }

    private ItemDefinition consumableWithoutAsset() {
        return ItemDefinition.create(
                "Boost",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                ItemContext.PROFILE,
                true,
                1000,
                true,
                new com.letraaletra.api.features.items.domain.PercentageTimedEffect(
                        com.letraaletra.api.features.items.domain.EffectType.XP_BOOST_PCT, 50, 60),
                null
        );
    }

    @Test
    @DisplayName("deletar cosmetico com asset deve checar permissao, deletar asset e repositorio")
    void deleteCosmeticWithAssetShouldCheckPermissionAndDeleteAssetAndRepository() {
        ItemDefinition definition = cosmeticWithAsset();
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));

        DeleteItemDefinitionOutput output = useCase.execute(new DeleteItemDefinitionInput(principal, definition.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.DELETE);
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemDefinitionRepository).delete(definition);
        assertSame(definition, output.definition());
    }

    @Test
    @DisplayName("deletar consumable sem asset nao deve deletar asset")
    void deleteConsumableWithoutAssetShouldNotDeleteAsset() {
        ItemDefinition definition = consumableWithoutAsset();
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));

        useCase.execute(new DeleteItemDefinitionInput(principal, definition.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.DELETE);
        verify(assetStorage, never()).delete(ArgumentMatchers.anyString());
        verify(itemDefinitionRepository).delete(definition);
    }

    @Test
    @DisplayName("definicao ausente deve falhar sem deletar asset nem repositorio")
    void missingShouldFailWithoutDeletingAssetOrRepository() {
        UUID missing = UUID.randomUUID();
        when(itemDefinitionRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(new DeleteItemDefinitionInput(principal, missing)));
        verify(assetStorage, never()).delete(ArgumentMatchers.anyString());
        verify(itemDefinitionRepository, never()).delete(ArgumentMatchers.any());
    }
}
