package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
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
@DisplayName("UpdateItemDefinitionUseCase Unit Tests")
class UpdateItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private ItemImageConverter imageConverter;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;
    private ItemDefinition definition;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
        definition = ItemDefinition.create(
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

    @Test
    @DisplayName("update deve checar admin, aplicar parcial, versionar e salvar")
    void updateShouldCheckAdminApplyPartialVersionAndSave() {
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(itemDefinitionRepository.findByName("Navy Avatar")).thenReturn(Optional.empty());
        when(assetStorage.copy("AVATAR/Blue Avatar.webp", "Navy Avatar", ItemCategory.AVATAR))
                .thenReturn("AVATAR/Navy Avatar.webp");

        UpdateItemDefinitionOutput output = useCase.execute(new UpdateItemDefinitionInput(
                principal, definition.getId(), "Navy Avatar", false, null, false));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.EDIT);
        assertEquals("Navy Avatar", output.definition().getName());
        assertEquals("AVATAR/Navy Avatar.webp", output.definition().getAssetPath());
        assertFalse(output.definition().isAvailable());
        assertEquals(2, output.definition().getVersion());
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemDefinitionRepository).save(definition);
    }

    @Test
    @DisplayName("novo asset deve subir, persistir novo path e deletar o antigo")
    void newAssetShouldUploadAndDeleteOld() {
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.eq("image/png")))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.eq("Blue Avatar"), ArgumentMatchers.eq(ItemCategory.AVATAR)))
                .thenReturn("AVATAR/Blue Avatar.webp");

        UpdateItemDefinitionOutput output = useCase.execute(new UpdateItemDefinitionInput(
                principal, definition.getId(), null, null,
                new ItemAssetUpload(new byte[]{1, 2, 3}, "image/png"), true));

        assertEquals("AVATAR/Blue Avatar.webp", output.definition().getAssetPath());
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemDefinitionRepository).save(definition);
    }

    @Test
    @DisplayName("falha no save apos upload deve deletar o novo asset")
    void saveFailureAfterUploadShouldDeleteNewAsset() {
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn("AVATAR/Blue Avatar.webp");
        org.mockito.Mockito.doThrow(new RuntimeException("Database offline"))
                .when(itemDefinitionRepository).save(ArgumentMatchers.any());

        assertThrows(RuntimeException.class, () -> useCase.execute(new UpdateItemDefinitionInput(
                principal, definition.getId(), null, null,
                new ItemAssetUpload(new byte[]{1}, "image/png"), true)));

        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
    }

    @Test
    @DisplayName("definição inexistente deve falhar")
    void missingDefinitionShouldFail() {
        UUID missing = UUID.randomUUID();
        when(itemDefinitionRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new UpdateItemDefinitionInput(principal, missing, "X", null, null, false)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("nome em uso por outra definição deve falhar")
    void nameInUseByAnotherDefinitionShouldFail() {
        ItemDefinition other = ItemDefinition.create(
                "Navy Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                ItemContext.PROFILE,
                false,
                null,
                false,
                null,
                "AVATAR/Navy Avatar.webp"
        );
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(itemDefinitionRepository.findByName("Navy Avatar")).thenReturn(Optional.of(other));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(
                new UpdateItemDefinitionInput(principal, definition.getId(), "Navy Avatar", null, null, false)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
