package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
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
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateItemDefinitionUseCase Unit Tests")
class CreateItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private ItemImageConverter imageConverter;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private CreateItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private CreateItemDefinitionInput input(String name) {
        return new CreateItemDefinitionInput(
                principal, name, ItemKind.COSMETIC, ItemCategory.AVATAR,
                ItemContext.PROFILE, false, null,
                new ItemAssetUpload(new byte[]{1, 2, 3}, "image/png")
        );
    }

    private CreateItemDefinitionInput consumableInput(String name) {
        return new CreateItemDefinitionInput(
                principal, name, ItemKind.CONSUMABLE, ItemCategory.XP_BOOST,
                ItemContext.PROFILE, true, new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), null
        );
    }

    @Test
    @DisplayName("create deve checar admin, subir asset, salvar e retornar a definição")
    void createShouldCheckAdminSaveAndReturnDefinition() {
        when(itemDefinitionRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.eq("image/png")))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.eq("Blue Avatar"), ArgumentMatchers.eq(ItemCategory.AVATAR)))
                .thenReturn("AVATAR/Blue Avatar.webp");

        CreateItemDefinitionOutput output = useCase.execute(input("Blue Avatar"));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.CREATE);
        assertEquals("Blue Avatar", output.definition().getName());
        assertEquals("AVATAR/Blue Avatar.webp", output.definition().getAssetPath());
        assertEquals(ItemContext.PROFILE, output.definition().getContext());
        verify(itemDefinitionRepository).save(output.definition());
    }

    @Test
    @DisplayName("consumable sem asset deve salvar sem upload e derivar stack 1000")
    void consumableWithoutAssetShouldSaveWithoutUpload() {
        when(itemDefinitionRepository.findByName("Boost")).thenReturn(Optional.empty());

        CreateItemDefinitionOutput output = useCase.execute(consumableInput("Boost"));

        assertNull(output.definition().getAssetPath());
        assertTrue(output.definition().isStackable());
        assertEquals(1000, output.definition().getMaxStack());
        verify(imageConverter, never()).convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(assetStorage, never()).upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(itemDefinitionRepository).save(output.definition());
    }

    @Test
    @DisplayName("cosmetico sem asset deve falhar sem upload nem save")
    void cosmeticWithoutAssetShouldFail() {
        CreateItemDefinitionInput withoutAsset = new CreateItemDefinitionInput(
                principal, "Avatar", ItemKind.COSMETIC, ItemCategory.AVATAR,
                ItemContext.PROFILE, false, null, null
        );
        when(itemDefinitionRepository.findByName("Avatar")).thenReturn(Optional.empty());

        assertThrows(InvalidItemException.class, () -> useCase.execute(withoutAsset));
        verify(assetStorage, never()).upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("falha no upload deve propagar sem salvar")
    void uploadFailureShouldPropagateWithoutSaving() {
        when(itemDefinitionRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Storage unavailable"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input("Blue Avatar")));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("falha na persistencia deve deletar o asset enviado")
    void saveFailureShouldDeleteUploadedAsset() {
        when(itemDefinitionRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn("AVATAR/Blue Avatar.webp");
        org.mockito.Mockito.doThrow(new RuntimeException("Database error"))
                .when(itemDefinitionRepository).save(ArgumentMatchers.any());

        assertThrows(RuntimeException.class, () -> useCase.execute(input("Blue Avatar")));
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
    }

    @Test
    @DisplayName("context ausente deve falhar (exigido)")
    void missingContextShouldFail() {
        when(itemDefinitionRepository.findByName("Emote")).thenReturn(Optional.empty());
        CreateItemDefinitionInput withoutContext = new CreateItemDefinitionInput(
                principal, "Emote", ItemKind.COSMETIC, ItemCategory.EMOTE,
                null, false, null, new ItemAssetUpload(new byte[]{1}, "image/png")
        );

        assertThrows(InvalidItemException.class, () -> useCase.execute(withoutContext));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("nome duplicado deve falhar sem salvar")
    void duplicateNameShouldFailWithoutSaving() {
        when(itemDefinitionRepository.findByName("Blue Avatar"))
                .thenReturn(Optional.of(mock(ItemDefinition.class)));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(input("Blue Avatar")));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
