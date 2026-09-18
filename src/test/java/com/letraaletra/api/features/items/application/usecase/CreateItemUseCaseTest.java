package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
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
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateItemUseCase Unit Tests")
class CreateItemUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private ItemImageConverter imageConverter;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private CreateItemUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private CreateItemInput input() {
        return new CreateItemInput(
                principal, "Blue Avatar", ItemKind.EQUIPPABLE, EquippableCategory.AVATAR,
                EquippableContext.PROFILE, null,
                new ItemAssetUpload(new byte[]{1, 2, 3}, "image/png")
        );
    }

    private CreateItemInput consumableInput() {
        return new CreateItemInput(
                principal, "Boost", ItemKind.CONSUMABLE, null,
                null, new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), null
        );
    }

    @Test
    @DisplayName("create deve checar admin, subir asset, salvar e retornar a definição")
    void createShouldCheckAdminSaveAndReturnDefinition() {
        when(itemRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.eq("image/png")))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.eq("Blue Avatar"), ArgumentMatchers.eq(EquippableCategory.AVATAR)))
                .thenReturn("AVATAR/Blue Avatar.webp");

        CreateItemOutput output = useCase.execute(input());

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.CREATE);
        assertEquals("Blue Avatar", output.item().getName());
        assertInstanceOf(EquippableItem.class, output.item());
        EquippableItem equippable = (EquippableItem) output.item();
        assertEquals("AVATAR/Blue Avatar.webp", equippable.getAssetPath());
        assertEquals(EquippableContext.PROFILE, equippable.getContext());
        verify(itemRepository).save(output.item());
    }

    @Test
    @DisplayName("consumable sem asset deve salvar sem upload e expor efeito")
    void consumableWithoutAssetShouldSaveWithoutUpload() {
        when(itemRepository.findByName("Boost")).thenReturn(Optional.empty());

        CreateItemOutput output = useCase.execute(consumableInput());

        assertInstanceOf(ConsumableItem.class, output.item());
        ConsumableItem consumable = (ConsumableItem) output.item();
        assertEquals(new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), consumable.getEffect());
        verify(imageConverter, never()).convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(assetStorage, never()).upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(itemRepository).save(output.item());
    }

    @Test
    @DisplayName("cosmetico sem asset deve falhar sem upload nem save")
    void cosmeticWithoutAssetShouldFail() {
        CreateItemInput withoutAsset = new CreateItemInput(
                principal, "Avatar", ItemKind.EQUIPPABLE, EquippableCategory.AVATAR,
                EquippableContext.PROFILE, null, null
        );
        when(itemRepository.findByName("Avatar")).thenReturn(Optional.empty());

        assertThrows(InvalidItemException.class, () -> useCase.execute(withoutAsset));
        verify(assetStorage, never()).upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("falha no upload deve propagar sem salvar")
    void uploadFailureShouldPropagateWithoutSaving() {
        when(itemRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Storage unavailable"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input()));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("falha na persistencia deve deletar o asset enviado")
    void saveFailureShouldDeleteUploadedAsset() {
        when(itemRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn("AVATAR/Blue Avatar.webp");
        org.mockito.Mockito.doThrow(new RuntimeException("Database error"))
                .when(itemRepository).save(ArgumentMatchers.any());

        assertThrows(RuntimeException.class, () -> useCase.execute(input()));
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
    }

    @Test
    @DisplayName("context ausente deve falhar (exigido)")
    void missingContextShouldFail() {
        when(itemRepository.findByName("Emote")).thenReturn(Optional.empty());
        CreateItemInput withoutContext = new CreateItemInput(
                principal, "Emote", ItemKind.EQUIPPABLE, EquippableCategory.EMOTE,
                null, null, new ItemAssetUpload(new byte[]{1}, "image/png")
        );

        assertThrows(InvalidItemException.class, () -> useCase.execute(withoutContext));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("nome duplicado deve falhar sem salvar")
    void duplicateNameShouldFailWithoutSaving() {
        when(itemRepository.findByName("Blue Avatar"))
                .thenReturn(Optional.of(mock(Item.class)));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(input()));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }
}
