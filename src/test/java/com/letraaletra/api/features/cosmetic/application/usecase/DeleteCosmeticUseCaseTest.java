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
import org.mockito.InOrder;
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
    private DeleteCosmeticUseCase useCase;

    private UUID auth;
    private UUID cosmeticId;
    private String assetPath;
    private Cosmetic cosmetic;

    @BeforeEach
    void setUp() {
        auth = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();
        assetPath = "assets/cosmetics/skin.png";

        cosmetic = mock(Cosmetic.class);
        lenient().when(cosmetic.getAssetPath()).thenReturn(assetPath);
    }

    @Test
    @DisplayName("Deve deletar o cosmético com sucesso quando as permissões e dados forem válidos")
    void execute_ShouldDeleteCosmeticAndStorageAsset_WhenInputIsValid() {
        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);
        doNothing().when(adminChecker).check(auth);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));

        DeleteCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(cosmetic, output.cosmetic());

        InOrder inOrder = inOrder(adminChecker, cosmeticRepository, storageGateway);
        inOrder.verify(adminChecker).check(auth);
        inOrder.verify(cosmeticRepository).find(cosmeticId);
        inOrder.verify(storageGateway).delete(assetPath);
        inOrder.verify(cosmeticRepository).delete(cosmetic);
    }

    @Test
    @DisplayName("Deve lançar exceção e interromper fluxo se o usuário não for administrador")
    void execute_ShouldThrowException_WhenUserIsNotAdmin() {
        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);

        doThrow(new UserIsNotAdminException())
                .when(adminChecker).check(auth);

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).find(any());
        verify(storageGateway, never()).delete(any());
        verify(cosmeticRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar CosmeticNotFoundException se o cosmético não for encontrado")
    void execute_ShouldThrowCosmeticNotFoundException_WhenCosmeticDoesNotExist() {
        DeleteCosmeticInput input = new DeleteCosmeticInput(auth, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(input));

        verify(adminChecker).check(auth);
        verify(storageGateway, never()).delete(any());
        verify(cosmeticRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve falhar e propagar erro caso a deleção no storage falhe")
    void execute_ShouldThrowException_WhenStorageGatewayFails() {
        DeleteCosmeticInput input = new DeleteCosmeticInput(cosmeticId, auth);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        doThrow(new RuntimeException("Storage communication failure")).when(storageGateway).delete(assetPath);

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).delete(cosmetic);
    }

    @Test
    @DisplayName("Deve falhar e propagar erro caso a remoção do banco de dados falhe")
    void execute_ShouldThrowException_WhenRepositoryDeleteFails() {
        DeleteCosmeticInput input = new DeleteCosmeticInput(cosmeticId, auth);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        doThrow(new RuntimeException("Database error")).when(cosmeticRepository).delete(cosmetic);

        assertThrows(RuntimeException.class, () -> useCase.execute(input));
    }
}