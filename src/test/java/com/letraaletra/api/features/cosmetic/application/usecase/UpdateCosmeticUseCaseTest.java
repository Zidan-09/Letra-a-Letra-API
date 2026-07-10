package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCosmeticUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private ImageConverter imageConverter;

    @Mock
    private AssetStorageGateway storageGateway;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateCosmeticUseCase useCase;

    private UUID adminId;
    private UUID cosmeticId;
    private Cosmetic cosmetic;

    private final String oldName = "Old Flame";
    private final CosmeticTypes oldType = CosmeticTypes.AVATAR;
    private final String oldAssetPath = "cosmetics/" + CosmeticTypes.AVATAR.name() + "/old_flame.webp";

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();

        cosmetic = mock(Cosmetic.class);
        lenient().when(cosmetic.getName()).thenReturn(oldName);
        lenient().when(cosmetic.getType()).thenReturn(oldType);
        lenient().when(cosmetic.getAssetPath()).thenReturn(oldAssetPath);
    }

    @Test
    @DisplayName("Deve atualizar dados básicos e mover o asset quando o nome ou tipo mudarem e não houver um novo asset")
    void execute_ShouldCopyAsset_WhenNameOrTypeChangesWithoutNewAsset() {
        String newName = "New Flame";
        CosmeticTypes newType = CosmeticTypes.AVATAR;
        String movedAssetPath = "cosmetics/aura/new_flame.webp";

        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn(newName);
        when(input.type()).thenReturn(newType);
        when(input.isNewAsset()).thenReturn(false);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        when(storageGateway.copy(oldAssetPath, newName, newType)).thenReturn(movedAssetPath);

        UpdateCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(cosmetic, output.cosmetic());

        InOrder inOrder = inOrder(adminChecker, cosmeticRepository, storageGateway, cosmetic);
        inOrder.verify(adminChecker).check(adminId);
        inOrder.verify(cosmeticRepository).find(cosmeticId);
        inOrder.verify(storageGateway).copy(oldAssetPath, newName, newType);
        inOrder.verify(cosmetic).setAssetPath(movedAssetPath);
        inOrder.verify(cosmetic).setName(newName);
        inOrder.verify(cosmetic).setType(newType);
        inOrder.verify(cosmetic).incrementVersion();
        inOrder.verify(cosmeticRepository).save(cosmetic);
    }

    @Test
    @DisplayName("Deve fazer upload de nova imagem e apagar o asset anterior se 'isNewAsset' for verdadeiro")
    void execute_ShouldUploadNewAssetAndDeleteOldOne_WhenIsNewAssetIsTrue() {
        String newName = "Old Flame";
        CosmeticTypes newType = CosmeticTypes.AVATAR;
        MultipartFile rawAsset = mock(MultipartFile.class);
        byte[] webpAsset = new byte[]{4, 5, 6};
        String newAssetPath = "cosmetics/" + CosmeticTypes.AVATAR.name() +"/old_flame.webp";

        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);

        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn(newName);
        when(input.type()).thenReturn(newType);
        when(input.isNewAsset()).thenReturn(true);
        when(input.asset()).thenReturn(rawAsset);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        when(imageConverter.convertToWebp(rawAsset)).thenReturn(webpAsset);
        when(storageGateway.upload(webpAsset, newName, newType)).thenReturn(newAssetPath);

        UpdateCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);

        InOrder inOrder = inOrder(imageConverter, storageGateway, cosmetic);
        inOrder.verify(imageConverter).convertToWebp(rawAsset);
        inOrder.verify(storageGateway).upload(webpAsset, newName, newType);
        inOrder.verify(cosmetic).setAssetPath(newAssetPath);
        inOrder.verify(storageGateway).delete(oldAssetPath);
        verify(storageGateway, never()).copy(any(), any(), any());
    }

    @Test
    @DisplayName("Deve apenas atualizar os dados sem interagir com o storage se nada mudou e não houver novo asset")
    void execute_ShouldNotTouchStorage_WhenNoChangesAndNoNewAsset() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn(oldName);
        when(input.type()).thenReturn(oldType);
        when(input.isNewAsset()).thenReturn(false);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));

        useCase.execute(input);

        verify(storageGateway, never()).copy(any(), any(), any());
        verify(storageGateway, never()).upload(any(), any(), any());
        verify(storageGateway, never()).delete(any());
        verify(cosmetic, never()).setAssetPath(any());
        verify(cosmeticRepository).save(cosmetic);
    }

    @Test
    @DisplayName("Deve lançar UserIsNotAdminException e interromper fluxo se o usuário não for administrador")
    void execute_ShouldThrowUserIsNotAdminException_WhenUserIsNotAdmin() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        doThrow(new UserIsNotAdminException()).when(adminChecker).check(adminId);

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).find(any());
        verify(storageGateway, never()).copy(any(), any(), any());
        verify(storageGateway, never()).upload(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar CosmeticNotFoundException se o cosmético editado não existir")
    void execute_ShouldThrowCosmeticNotFoundException_WhenCosmeticDoesNotExist() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(input));

        verify(storageGateway, never()).copy(any(), any(), any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar erro estruturado se a conversão do novo asset falhar")
    void execute_ShouldPropagateException_WhenImageConversionFails() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.isNewAsset()).thenReturn(true);
        when(input.asset()).thenReturn(mock(MultipartFile.class));

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        when(imageConverter.convertToWebp(any())).thenThrow(new IllegalArgumentException("Invalid format"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));

        verify(storageGateway, never()).upload(any(), any(), any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando outro cosmético já possui o mesmo nome e tipo")
    void execute_ShouldThrowException_WhenNewNameCollidesWithAnotherCosmetic() {
        String collidingName = "Existing Cosmetic Name";

        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn(collidingName);
        when(input.type()).thenReturn(oldType);

        Cosmetic anotherCosmetic = mock(Cosmetic.class);
        when(anotherCosmetic.getId()).thenReturn(UUID.randomUUID());
        when(anotherCosmetic.getType()).thenReturn(oldType);

        when(cosmeticRepository.find(cosmeticId))
                .thenReturn(Optional.of(cosmetic));

        when(cosmeticRepository.findByName(collidingName))
                .thenReturn(Optional.of(anotherCosmetic));

        assertThrows(
                InvalidCosmeticException.class,
                () -> useCase.execute(input)
        );

        verify(cosmeticRepository).findByName(collidingName);
        verify(cosmeticRepository, never()).save(any());
        verifyNoInteractions(storageGateway);
    }

    @Test
    @DisplayName("Deve tratar falhas de IO no Storage e evitar inconsistência de dados no banco")
    void execute_ShouldPropagateException_WhenStorageMoveFails() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn("New Name");
        when(input.type()).thenReturn(oldType);
        when(input.isNewAsset()).thenReturn(false);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        when(storageGateway.copy(any(), any(), any())).thenThrow(new RuntimeException("Storage link timeout"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(cosmetic, never()).setName(any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve disparar compensação/deleção preventiva do novo asset se o save no banco falhar (Comportamento Desejado/Ausente - Consistência)")
    void execute_ShouldDeleteUploadedAsset_WhenDatabaseSaveFailsAfterNewUpload() {
        UpdateCosmeticInput input = mock(UpdateCosmeticInput.class);
        when(input.auth()).thenReturn(adminId);
        when(input.id()).thenReturn(cosmeticId);
        when(input.name()).thenReturn(oldName);
        when(input.type()).thenReturn(oldType);
        when(input.isNewAsset()).thenReturn(true);
        when(input.asset()).thenReturn(mock(MultipartFile.class));

        String uploadedPath = "cosmetics/" + oldType.name() + "/new_uploaded.webp";

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        when(imageConverter.convertToWebp(any())).thenReturn(new byte[]{2});
        when(storageGateway.upload(any(), any(), any())).thenReturn(uploadedPath);

        doThrow(new RuntimeException("Database offline")).when(cosmeticRepository).save(cosmetic);

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(storageGateway).delete(uploadedPath);
    }
}