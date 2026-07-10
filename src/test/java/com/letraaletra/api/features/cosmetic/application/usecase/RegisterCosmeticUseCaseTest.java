package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCosmeticUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AssetStorageGateway storageGateway;

    @Mock
    private ImageConverter imageConverter;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RegisterCosmeticUseCase useCase;

    private UUID adminId;
    private String cosmeticName;
    private CosmeticTypes cosmeticType;
    private MultipartFile rawAsset;
    private byte[] webpAsset;
    private String uploadedAssetPath;
    private Cosmetic mockCosmetic;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        cosmeticName = "Gold Skin";
        cosmeticType = CosmeticTypes.AVATAR;
        rawAsset = mock(MultipartFile.class);
        webpAsset = new byte[]{4, 5, 6};
        uploadedAssetPath = "cosmetics/skins/gold_skin.webp";
        mockCosmetic = mock(Cosmetic.class);
    }

    @Test
    @DisplayName("Deve registrar um cosmético com sucesso quando os dados forem válidos e o usuário for admin")
    void execute_ShouldRegisterCosmetic_WhenInputIsValidAndUserIsAdmin() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        try (MockedStatic<Cosmetic> cosmeticStatic = mockStatic(Cosmetic.class)) {
            when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.empty());
            when(imageConverter.convertToWebp(rawAsset)).thenReturn(webpAsset);
            when(storageGateway.upload(webpAsset, cosmeticName, cosmeticType)).thenReturn(uploadedAssetPath);

            cosmeticStatic.when(() -> Cosmetic.create(cosmeticName, cosmeticType, uploadedAssetPath))
                    .thenReturn(mockCosmetic);

            RegisterCosmeticOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockCosmetic, output.cosmetic());

            InOrder inOrder = inOrder(adminChecker, cosmeticRepository, imageConverter, storageGateway);
            inOrder.verify(adminChecker).check(adminId);
            inOrder.verify(cosmeticRepository).findByName(cosmeticName);
            inOrder.verify(imageConverter).convertToWebp(rawAsset);
            inOrder.verify(storageGateway).upload(webpAsset, cosmeticName, cosmeticType);
            inOrder.verify(cosmeticRepository).save(mockCosmetic);
        }
    }

    @Test
    @DisplayName("Deve lançar UserIsNotAdminException e interromper o fluxo quando o usuário não for administrador")
    void execute_ShouldThrowUserIsNotAdminException_WhenUserIsNotAdmin() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        doThrow(new UserIsNotAdminException()).when(adminChecker).check(adminId);

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).findByName(any());
        verify(imageConverter, never()).convertToWebp(any());
        verify(storageGateway, never()).upload(any(), any(), any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException se o cosmético já existir com o mesmo nome")
    void execute_ShouldThrowException_WhenCosmeticAlreadyExistsWithName() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);
        Cosmetic existingCosmetic = mock(Cosmetic.class);

        when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.of(existingCosmetic));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> useCase.execute(input));
        assertEquals("cosmetic_already_exists", exception.getMessage());

        verify(adminChecker).check(adminId);
        verify(imageConverter, never()).convertToWebp(any());
        verify(storageGateway, never()).upload(any(), any(), any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar e propagar erro caso a conversão de imagem falhe")
    void execute_ShouldPropagateException_WhenImageConversionFails() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(rawAsset)).thenThrow(new IllegalArgumentException("Invalid image format"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));

        verify(storageGateway, never()).upload(any(), any(), any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar e propagar erro caso o upload para o Storage falhe")
    void execute_ShouldPropagateException_WhenStorageUploadFails() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.empty());
        when(imageConverter.convertToWebp(rawAsset)).thenReturn(webpAsset);
        when(storageGateway.upload(webpAsset, cosmeticName, cosmeticType)).thenThrow(new RuntimeException("Storage unavailable"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar e propagar erro caso a persistência no banco falhe")
    void execute_ShouldPropagateException_WhenRepositorySaveFails() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        try (MockedStatic<Cosmetic> cosmeticStatic = mockStatic(Cosmetic.class)) {
            when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.empty());
            when(imageConverter.convertToWebp(rawAsset)).thenReturn(webpAsset);
            when(storageGateway.upload(webpAsset, cosmeticName, cosmeticType)).thenReturn(uploadedAssetPath);

            cosmeticStatic.when(() -> Cosmetic.create(cosmeticName, cosmeticType, uploadedAssetPath)).thenReturn(mockCosmetic);
            doThrow(new RuntimeException("Database error")).when(cosmeticRepository).save(mockCosmetic);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));
        }
    }

    @Test
    @DisplayName("Deve validar contra entradas de texto vazias ou em branco (Comportamento Desejado/Ausente)")
    void execute_ShouldThrowException_WhenNameIsEmptyOrBlank() {
        RegisterCosmeticInput emptyNameInput = new RegisterCosmeticInput(adminId, "", cosmeticType, rawAsset);
        RegisterCosmeticInput blankNameInput = new RegisterCosmeticInput(adminId, "   ", cosmeticType, rawAsset);

        // Cenário onde o use case deveria barrar nomes inválidos antes do processamento pesado
        when(cosmeticRepository.findByName("")).thenThrow(new IllegalArgumentException("Cosmetic name cannot be blank"));
        when(cosmeticRepository.findByName("   ")).thenThrow(new IllegalArgumentException("Cosmetic name cannot be blank"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(emptyNameInput));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(blankNameInput));

        verify(imageConverter, never()).convertToWebp(any());
    }

    @Test
    @DisplayName("Deve disparar compensação/reversão no storage se a persistência falhar (Comportamento Desejado/Ausente - Consistência Transacional)")
    void execute_ShouldDeleteUploadedAsset_WhenDatabasePersistFailsAfterUpload() {
        RegisterCosmeticInput input = new RegisterCosmeticInput(adminId, cosmeticName, cosmeticType, rawAsset);

        try (MockedStatic<Cosmetic> cosmeticStatic = mockStatic(Cosmetic.class)) {
            when(cosmeticRepository.findByName(cosmeticName)).thenReturn(Optional.empty());
            when(imageConverter.convertToWebp(rawAsset)).thenReturn(webpAsset);
            when(storageGateway.upload(webpAsset, cosmeticName, cosmeticType)).thenReturn(uploadedAssetPath);

            cosmeticStatic.when(() -> Cosmetic.create(cosmeticName, cosmeticType, uploadedAssetPath)).thenReturn(mockCosmetic);
            doThrow(new RuntimeException("Database down")).when(cosmeticRepository).save(mockCosmetic);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(storageGateway).delete(uploadedAssetPath);
        }
    }
}