package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCosmeticUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AssetStorageGateway storageGateway;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DeleteCosmeticUseCase deleteCosmeticUseCase;

    private UUID auth;

    @BeforeEach
    void setup() {
        auth = UUID.randomUUID();
    }

    @Test
    @DisplayName("should delete the cosmetic correctly")
    void shouldDeleteCosmeticWithSuccessWhenUserIsAdminAndCosmeticExists() {
        UUID cosmeticId = UUID.randomUUID();
        String assetPath = "path/to/cosmetic/asset.png";

        doNothing().when(adminChecker).check(auth);

        Cosmetic cosmetic = mock(Cosmetic.class);
        when(cosmetic.getAssetPath()).thenReturn(assetPath);

        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));

        DeleteCosmeticOutput output = deleteCosmeticUseCase.execute(input);

        assertNotNull(output);
        assertEquals(cosmetic, output.cosmetic());

        verify(storageGateway, times(1)).delete(assetPath);
        verify(cosmeticRepository, times(1)).delete(cosmeticId);
    }

    @Test
    @DisplayName("should throw an UserIsNotAdminException when user is not admin")
    void shouldThrowUserIsNotAdminExceptionWhenUserIsNotAdmin() {
        UUID cosmeticId = UUID.randomUUID();

        doThrow(new UserIsNotAdminException()).when(adminChecker).check(auth);

        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);

        assertThrows(UserIsNotAdminException.class, () -> deleteCosmeticUseCase.execute(input));

        verifyNoInteractions(cosmeticRepository);
        verifyNoInteractions(storageGateway);
    }

    @Test
    @DisplayName("should throw a CosmeticNotFoundException when cosmetic is not found")
    void shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist() {
        UUID cosmeticId = UUID.randomUUID();

        doNothing().when(adminChecker).check(auth);

        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> deleteCosmeticUseCase.execute(input));

        verify(storageGateway, never()).delete(anyString());
        verify(cosmeticRepository, never()).delete(any(UUID.class));
    }
}