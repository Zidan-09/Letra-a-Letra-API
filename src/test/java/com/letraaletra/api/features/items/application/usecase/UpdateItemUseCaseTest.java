package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.input.UpdateItemInput;
import com.letraaletra.api.features.items.application.output.UpdateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
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
@DisplayName("UpdateItemUseCase Unit Tests")
class UpdateItemUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemAssetStorage assetStorage;

    @Mock
    private ItemImageConverter imageConverter;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateItemUseCase useCase;

    private AuthenticatedUser principal;
    private Item item;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
        item = EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "AVATAR/Blue Avatar.webp"
        );
    }

    @Test
    @DisplayName("update deve checar admin, aplicar parcial, versionar e salvar")
    void updateShouldCheckAdminApplyPartialVersionAndSave() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.findByName("Navy Avatar")).thenReturn(Optional.empty());
        when(assetStorage.copy("AVATAR/Blue Avatar.webp", "Navy Avatar", EquippableCategory.AVATAR))
                .thenReturn("AVATAR/Navy Avatar.webp");

        UpdateItemOutput output = useCase.execute(new UpdateItemInput(
                principal, item.getId(), "Navy Avatar", false, null, false));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.EDIT);
        assertEquals("Navy Avatar", output.item().getName());
        assertTrue(output.item() instanceof EquippableItem);
        assertEquals("AVATAR/Navy Avatar.webp", ((EquippableItem) output.item()).getAssetPath());
        assertFalse(output.item().isAvailable());
        assertEquals(2, output.item().getVersion());
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("novo asset deve subir, persistir novo path e deletar o antigo")
    void newAssetShouldUploadAndDeleteOld() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.eq("image/png")))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.eq("Blue Avatar"), ArgumentMatchers.eq(EquippableCategory.AVATAR)))
                .thenReturn("AVATAR/Blue Avatar.webp");

        UpdateItemOutput output = useCase.execute(new UpdateItemInput(
                principal, item.getId(), null, null,
                new ItemAssetUpload(new byte[]{1, 2, 3}, "image/png"), true));

        assertTrue(output.item() instanceof EquippableItem);
        assertEquals("AVATAR/Blue Avatar.webp", ((EquippableItem) output.item()).getAssetPath());
        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("falha no save apos upload deve deletar o novo asset")
    void saveFailureAfterUploadShouldDeleteNewAsset() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(imageConverter.convertToWebp(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});
        when(assetStorage.upload(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn("AVATAR/Blue Avatar.webp");
        org.mockito.Mockito.doThrow(new RuntimeException("Database offline"))
                .when(itemRepository).save(ArgumentMatchers.any());

        assertThrows(RuntimeException.class, () -> useCase.execute(new UpdateItemInput(
                principal, item.getId(), null, null,
                new ItemAssetUpload(new byte[]{1}, "image/png"), true)));

        verify(assetStorage).delete("AVATAR/Blue Avatar.webp");
    }

    @Test
    @DisplayName("definição inexistente deve falhar")
    void missingDefinitionShouldFail() {
        UUID missing = UUID.randomUUID();
        when(itemRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new UpdateItemInput(principal, missing, "X", null, null, false)));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("nome em uso por outra definição deve falhar")
    void nameInUseByAnotherDefinitionShouldFail() {
        Item other = EquippableItem.create(
                "Navy Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "AVATAR/Navy Avatar.webp"
        );
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.findByName("Navy Avatar")).thenReturn(Optional.of(other));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(
                new UpdateItemInput(principal, item.getId(), "Navy Avatar", null, null, false)));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }
}
