package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
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

    @InjectMocks
    private DeleteCosmeticUseCase deleteCosmeticUseCase;

    @Test
    @DisplayName("should delete the cosmetic correctly")
    void shouldDeleteCosmeticWithSuccessWhenUserIsAdminAndCosmeticExists() {
        UUID cosmeticId = UUID.randomUUID();
        String assetPath = "path/to/cosmetic/asset.png";

        User adminUser = mock(User.class);
        when(adminUser.isAdmin()).thenReturn(true);

        Cosmetic cosmetic = mock(Cosmetic.class);
        when(cosmetic.getAssetPath()).thenReturn(assetPath);

        DeleteCosmeticInput input = new DeleteCosmeticInput(adminUser, cosmeticId);

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
        User regularUser = mock(User.class);
        when(regularUser.isAdmin()).thenReturn(false);

        DeleteCosmeticInput input = new DeleteCosmeticInput(regularUser, cosmeticId);

        assertThrows(UserIsNotAdminException.class, () -> deleteCosmeticUseCase.execute(input));

        verifyNoInteractions(cosmeticRepository);
        verifyNoInteractions(storageGateway);
    }

    @Test
    @DisplayName("should throw a CosmeticNotFoundException when cosmetic is not found")
    void shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist() {
        UUID cosmeticId = UUID.randomUUID();
        User adminUser = mock(User.class);
        when(adminUser.isAdmin()).thenReturn(true);

        DeleteCosmeticInput input = new DeleteCosmeticInput(adminUser, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> deleteCosmeticUseCase.execute(input));

        verify(storageGateway, never()).delete(anyString());
        verify(cosmeticRepository, never()).delete(any(UUID.class));
    }
}