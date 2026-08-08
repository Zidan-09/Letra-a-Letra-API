package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.SamePasswordException;
import com.letraaletra.api.features.user.domain.repository.reset.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordUseCase Unit Tests")
class ResetPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private ResetCodeRepository codeRepository;

    @InjectMocks
    private ResetPasswordUseCase useCase;

    private String rawCode;
    private String codeHash;
    private String currentPasswordHash;
    private String newRawPassword;
    private String newPasswordHash;
    private UUID userId;
    private ResetPasswordInput input;
    private PasswordResetCode mockResetCode;
    private User mockUser;

    @BeforeEach
    void setUp() {
        rawCode = "123456";
        codeHash = "hashed_123456";
        currentPasswordHash = "$2a$10$oldPasswordHash";
        newRawPassword = "NewSecretPassword123!";
        newPasswordHash = "$2a$10$newPasswordHash";
        userId = UUID.randomUUID();

        input = new ResetPasswordInput(newRawPassword, rawCode);
        mockResetCode = mock(PasswordResetCode.class);
        mockUser = mock(User.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve redefinir a senha com sucesso, marcar o código como usado e salvar o usuário e o código")
        void execute_WhenValidCodeAndNewPassword_ShouldResetPasswordAndSaveEntities() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.of(mockResetCode));
            when(mockResetCode.getUserId()).thenReturn(userId);
            when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getPasswordHash()).thenReturn(currentPasswordHash);
            when(passwordService.matches(newRawPassword, currentPasswordHash)).thenReturn(false);
            when(passwordService.hash(newRawPassword)).thenReturn(newPasswordHash);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(tokenHashService, codeRepository, mockResetCode, userRepository, passwordService, mockUser);
            inOrder.verify(tokenHashService).hash(rawCode);
            inOrder.verify(codeRepository).findByCodeHash(codeHash);
            inOrder.verify(mockResetCode).validate(codeHash);
            inOrder.verify(userRepository).find(userId);
            inOrder.verify(passwordService).matches(newRawPassword, currentPasswordHash);
            inOrder.verify(mockResetCode).markAsUsed();
            inOrder.verify(passwordService).hash(newRawPassword);
            inOrder.verify(mockUser).changePassword(newPasswordHash);

            verify(userRepository, times(1)).save(mockUser);
            verify(codeRepository, times(1)).save(mockResetCode);
        }
    }

    @Nested
    @DisplayName("Validação do Código de Redefinição e Exceções")
    class ResetCodeValidationFailures {

        @Test
        @DisplayName("Deve lançar InvalidTokenException quando o código fornecido não for encontrado no repositório")
        void execute_WhenCodeNotFound_ShouldThrowInvalidTokenException() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.empty());

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(codeRepository, times(1)).findByCodeHash(codeHash);
            verifyNoInteractions(userRepository, passwordService);
        }

        @Test
        @DisplayName("Deve propagar InvalidTokenException quando a validação do código falhar (ex: usado ou expirado)")
        void execute_WhenCodeValidationFails_ShouldPropagateInvalidTokenException() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.of(mockResetCode));
            doThrow(new InvalidTokenException()).when(mockResetCode).validate(codeHash);

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(codeRepository, times(1)).findByCodeHash(codeHash);
            verify(mockResetCode, times(1)).validate(codeHash);
            verifyNoInteractions(userRepository, passwordService);
            verify(codeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar InvalidTokenException quando o usuário associado ao código não for encontrado")
        void execute_WhenUserNotFound_ShouldThrowInvalidTokenException() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.of(mockResetCode));
            when(mockResetCode.getUserId()).thenReturn(userId);
            when(userRepository.find(userId)).thenReturn(Optional.empty());

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(mockResetCode, times(1)).validate(codeHash);
            verify(userRepository, times(1)).find(userId);
            verifyNoInteractions(passwordService);
            verify(codeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validação de senha de Domínio")
    class PasswordValidationFailures {

        @Test
        @DisplayName("Deve lançar SamePasswordException quando a nova senha for igual à senha atual")
        void execute_WhenNewPasswordMatchesOldPassword_ShouldThrowSamePasswordExceptionAndNotMarkCodeAsUsed() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.of(mockResetCode));
            when(mockResetCode.getUserId()).thenReturn(userId);
            when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getPasswordHash()).thenReturn(currentPasswordHash);
            when(passwordService.matches(newRawPassword, currentPasswordHash)).thenReturn(true);

            assertThrows(
                    SamePasswordException.class,
                    () -> useCase.execute(input)
            );

            verify(passwordService, times(1)).matches(newRawPassword, currentPasswordHash);
            verify(mockResetCode, never()).markAsUsed();
            verify(mockUser, never()).changePassword(any());
            verify(userRepository, never()).save(any());
            verify(codeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar as alterações do usuário")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            when(tokenHashService.hash(rawCode)).thenReturn(codeHash);
            when(codeRepository.findByCodeHash(codeHash)).thenReturn(Optional.of(mockResetCode));
            when(mockResetCode.getUserId()).thenReturn(userId);
            when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getPasswordHash()).thenReturn(currentPasswordHash);
            when(passwordService.matches(newRawPassword, currentPasswordHash)).thenReturn(false);
            when(passwordService.hash(newRawPassword)).thenReturn(newPasswordHash);

            doThrow(new RuntimeException("Erro de conexão com o banco de dados"))
                    .when(userRepository).save(mockUser);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro de conexão com o banco de dados", exception.getMessage());
            verify(userRepository, times(1)).save(mockUser);
            verify(codeRepository, never()).save(any());
        }
    }
}