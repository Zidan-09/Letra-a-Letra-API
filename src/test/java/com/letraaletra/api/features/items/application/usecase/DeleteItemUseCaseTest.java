package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.DeleteItemInput;
import com.letraaletra.api.features.items.application.output.DeleteItemOutput;
import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.EquippableItem;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteItemUseCase Unit Tests")
class DeleteItemUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DeleteItemUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private Item cosmeticWithAsset() {
        return EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "AVATAR/Blue Avatar.webp"
        );
    }

    private Item consumableWithoutAsset() {
        return ConsumableItem.create(
                "Boost",
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60)
        );
    }

    @Test
    @DisplayName("deletar cosmetico com asset deve checar permissao, deletar asset e repositorio")
    void deleteCosmeticWithAssetShouldCheckPermissionAndDeleteAssetAndRepository() {
        Item item = cosmeticWithAsset();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        DeleteItemOutput output = useCase.execute(new DeleteItemInput(principal, item.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.DELETE);
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemRepository).delete(item);
        assertSame(item, output.item());
    }

    @Test
    @DisplayName("deletar consumable sem asset nao deve deletar asset")
    void deleteConsumableWithoutAssetShouldNotDeleteAsset() {
        Item item = consumableWithoutAsset();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        useCase.execute(new DeleteItemInput(principal, item.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.DELETE);
        verify(assetStorage, never()).delete(ArgumentMatchers.anyString());
        verify(itemRepository).delete(item);
    }

    @Test
    @DisplayName("definicao ausente deve falhar sem deletar asset nem repositorio")
    void missingShouldFailWithoutDeletingAssetOrRepository() {
        UUID missing = UUID.randomUUID();
        when(itemRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(new DeleteItemInput(principal, missing)));
        verify(assetStorage, never()).delete(ArgumentMatchers.anyString());
        verify(itemRepository, never()).delete(ArgumentMatchers.any());
    }
}
