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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private MultipartFile asset;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RegisterCosmeticUseCase useCase;

    private RegisterCosmeticInput input;
    private UUID auth;

    @BeforeEach
    void setup() {
        auth = UUID.randomUUID();

        input = new RegisterCosmeticInput(
                auth,
                "Old Man Avatar",
                CosmeticTypes.AVATAR,
                asset
        );
    }

    @Test
    void shouldRegisterCosmeticSuccessfully() {
        byte[] webpImage = "webp-image".getBytes();
        String assetPath = "avatars/old-man-avatar-free.webp";

        doNothing().when(adminChecker).check(auth);

        when(cosmeticRepository.findByName(input.name()))
                .thenReturn(Optional.empty());

        when(imageConverter.convertToWebp(asset))
                .thenReturn(webpImage);

        when(storageGateway.upload(
                webpImage,
                input.name(),
                input.type()
        )).thenReturn(assetPath);

        RegisterCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);

        ArgumentCaptor<Cosmetic> cosmeticCaptor =
                ArgumentCaptor.forClass(Cosmetic.class);

        verify(cosmeticRepository).save(cosmeticCaptor.capture());

        Cosmetic savedCosmetic = cosmeticCaptor.getValue();

        assertEquals(input.name(), savedCosmetic.getName());
        assertEquals(input.type(), savedCosmetic.getType());
        assertEquals(assetPath, savedCosmetic.getAssetPath());
        assertEquals(1, savedCosmetic.getVersion());

        verify(imageConverter).convertToWebp(asset);
        verify(storageGateway).upload(
                webpImage,
                input.name(),
                input.type()
        );
    }

    @Test
    void shouldThrowExceptionWhenCosmeticAlreadyExists() {
        Cosmetic existingCosmetic = Cosmetic.create(
                "Old Man Avatar",
                CosmeticTypes.AVATAR,
                "asset.webp"
        );

        doNothing().when(adminChecker).check(auth);

        when(cosmeticRepository.findByName(input.name()))
                .thenReturn(Optional.of(existingCosmetic));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(input)
        );

        assertEquals(
                "cosmetic_already_exists",
                exception.getMessage()
        );

        verify(cosmeticRepository, never()).save(any());
        verify(imageConverter, never()).convertToWebp(any());
        verify(storageGateway, never()).upload(any(), any(), any());
    }

    @Test
    @DisplayName("should throws an UserIsNotAdminException when user is not admin")
    void shouldThrowExceptionWhenUserIsNotAdmin() {
        doThrow(new UserIsNotAdminException()).when(adminChecker).check(auth);

        RuntimeException exception = assertThrows(
                UserIsNotAdminException.class,
                () -> useCase.execute(input)
        );

        assertEquals(
                "invalid_user_data",
                exception.getMessage()
        );

        verify(cosmeticRepository, never()).findByName(any());
        verify(cosmeticRepository, never()).save(any());
        verify(imageConverter, never()).convertToWebp(any());
        verify(storageGateway, never()).upload(any(), any(), any());
    }
}