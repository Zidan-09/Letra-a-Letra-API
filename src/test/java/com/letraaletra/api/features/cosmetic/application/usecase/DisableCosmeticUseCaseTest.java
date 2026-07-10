package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
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
class DisableCosmeticUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DisableCosmeticUseCase useCase;

    private UUID adminId;
    private UUID cosmeticId;
    private Cosmetic cosmetic;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();
        cosmetic = mock(Cosmetic.class);
    }

    @Test
    @DisplayName("Deve desabilitar o cosmético com sucesso quando o usuário for admin e o cosmético existir")
    void execute_ShouldDisableCosmetic_WhenUserIsAdminAndCosmeticExists() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));

        DisableCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(cosmetic, output.cosmetic());

        InOrder inOrder = inOrder(adminChecker, cosmeticRepository, cosmetic);
        inOrder.verify(adminChecker).check(adminId);
        inOrder.verify(cosmeticRepository).find(cosmeticId);
        inOrder.verify(cosmetic).setAvailable(false);
        inOrder.verify(cosmeticRepository).save(cosmetic);
    }

    @Test
    @DisplayName("Deve lançar UserIsNotAdminException e interromper fluxo quando o usuário não for administrador")
    void execute_ShouldThrowUserIsNotAdminException_WhenUserIsNotAdmin() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        doThrow(new UserIsNotAdminException()).when(adminChecker).check(adminId);

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).find(any());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar CosmeticNotFoundException quando o cosmético não for encontrado")
    void execute_ShouldThrowCosmeticNotFoundException_WhenCosmeticDoesNotExist() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(input));

        verify(adminChecker).check(adminId);
        verify(cosmetic, never()).setAvailable(anyBoolean());
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar erro genérico se o repositório falhar na busca")
    void execute_ShouldPropagateException_WhenRepositoryFindFails() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenThrow(new RuntimeException("Database timeout"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar erro genérico se o repositório falhar na persistência")
    void execute_ShouldPropagateException_WhenRepositorySaveFails() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));
        doThrow(new RuntimeException("Database constraints failure")).when(cosmeticRepository).save(cosmetic);

        assertThrows(RuntimeException.class, () -> useCase.execute(input));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o ID de autenticação for nulo (Comportamento Desejado/Ausente)")
    void execute_ShouldThrowException_WhenAuthIdIsNull() {
        DisableCosmeticInput input = new DisableCosmeticInput(null, cosmeticId);

        doThrow(new IllegalArgumentException("Auth ID cannot be null")).when(adminChecker).check(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));
        verify(cosmeticRepository, never()).find(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o ID do cosmético for nulo (Comportamento Desejado/Ausente)")
    void execute_ShouldThrowException_WhenCosmeticIdIsNull() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, null);

        when(cosmeticRepository.find(null)).thenThrow(new IllegalArgumentException("Cosmetic ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));
        verify(cosmeticRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve garantir comportamento idempotente se o cosmético já estiver desabilitado (Comportamento Desejado/Ausente)")
    void execute_ShouldHandleIdempotency_WhenCosmeticIsAlreadyDisabled() {
        DisableCosmeticInput input = new DisableCosmeticInput(adminId, cosmeticId);

        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(cosmetic));

        DisableCosmeticOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(cosmetic).setAvailable(false);
        verify(cosmeticRepository).save(cosmetic);
    }
}