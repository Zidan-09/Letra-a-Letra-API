package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.reset.PasswordResetCode;
import com.letraaletra.api.features.user.domain.reset.exception.MaxAttemptsExceededException;
import com.letraaletra.api.features.user.domain.reset.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
@DisplayName("VerifyResetCodeUseCase Unit Tests")
class VerifyResetCodeUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResetCodeRepository codeRepository;

    @Mock
    private TokenHashService tokenHashService;

    @InjectMocks
    private VerifyResetCodeUseCase useCase;

    private String email;
    private UUID userId;
    private String rawCode;
    private String hashedCode;
    private VerifyResetCodeInput input;
    private User mockUser;
    private PasswordResetCode mockResetCode;

    @BeforeEach
    void setUp() {
        email = "user@example.com";
        userId = UUID.randomUUID();
        rawCode = "123456";
        hashedCode = "hashed_123456";

        input = new VerifyResetCodeInput(email, rawCode);
        mockUser = mock(User.class);
        mockResetCode = mock(PasswordResetCode.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve gerar o hash do código, validar o token e salvar o código no repositório")
        void execute_WhenCodeIsValid_ShouldHashValidateAndSave() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(codeRepository.findActiveByUserId(userId)).thenReturn(Optional.of(mockResetCode));

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(tokenHashService, userRepository, codeRepository, mockResetCode);
            inOrder.verify(tokenHashService).hash(rawCode);
            inOrder.verify(userRepository).findByEmail(email);
            inOrder.verify(codeRepository).findActiveByUserId(userId);
            inOrder.verify(mockResetCode).validate(hashedCode);
            inOrder.verify(codeRepository).save(mockResetCode);
        }
    }

    @Nested
    @DisplayName("Exceções de Domínio e Validação do Código")
    class DomainExceptions {

        @Test
        @DisplayName("Deve lançar InvalidTokenException quando o usuário não for encontrado")
        void execute_WhenUserNotFound_ShouldThrowInvalidTokenExceptionAndNotValidateOrSave() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(userRepository, times(1)).findByEmail(email);
            verify(codeRepository, never()).findActiveByUserId(any());
            verify(mockResetCode, never()).validate(any());
            verify(codeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar InvalidTokenException quando não houver código ativo para o usuário")
        void execute_WhenCodeNotFound_ShouldThrowInvalidTokenExceptionAndNotValidateOrSave() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(codeRepository.findActiveByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(userRepository, times(1)).findByEmail(email);
            verify(codeRepository, times(1)).findActiveByUserId(userId);
            verify(mockResetCode, never()).validate(any());
            verify(codeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar InvalidTokenException quando o código estiver expirado, usado ou inválido no domínio e salvar a tentativa")
        void execute_WhenCodeValidationFails_ShouldPropagateInvalidTokenExceptionAndSave() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(codeRepository.findActiveByUserId(userId)).thenReturn(Optional.of(mockResetCode));
            doThrow(new InvalidTokenException()).when(mockResetCode).validate(hashedCode);

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(userRepository, times(1)).findByEmail(email);
            verify(codeRepository, times(1)).findActiveByUserId(userId);
            verify(mockResetCode, times(1)).validate(hashedCode);
            verify(codeRepository, times(1)).save(mockResetCode);
        }

        @Test
        @DisplayName("Deve propagar MaxAttemptsExceededException quando o código exceder o limite de tentativas no domínio")
        void execute_WhenMaxAttemptsExceeded_ShouldPropagateExceptionAndNotSave() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(codeRepository.findActiveByUserId(userId)).thenReturn(Optional.of(mockResetCode));
            doThrow(new MaxAttemptsExceededException()).when(mockResetCode).validate(hashedCode);

            assertThrows(
                    MaxAttemptsExceededException.class,
                    () -> useCase.execute(input)
            );

            verify(tokenHashService, times(1)).hash(rawCode);
            verify(userRepository, times(1)).findByEmail(email);
            verify(codeRepository, times(1)).findActiveByUserId(userId);
            verify(mockResetCode, times(1)).validate(hashedCode);
            verify(codeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Falhas nos Serviços e Repositórios")
    class ServiceAndRepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o TokenHashService falhe ao efetuar o hash")
        void execute_WhenTokenHashServiceFails_ShouldPropagateException() {
            when(tokenHashService.hash(rawCode)).thenThrow(new RuntimeException("Erro ao gerar hash"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao gerar hash", exception.getMessage());
            verify(tokenHashService, times(1)).hash(rawCode);
            verifyNoInteractions(userRepository, codeRepository);
        }

        @Test
        @DisplayName("Deve propagar exceção caso o ResetCodeRepository falhe ao salvar o código")
        void execute_WhenRepositorySaveFails_ShouldPropagateException() {
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(codeRepository.findActiveByUserId(userId)).thenReturn(Optional.of(mockResetCode));
            doThrow(new RuntimeException("Erro ao salvar no banco de dados"))
                    .when(codeRepository).save(mockResetCode);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao salvar no banco de dados", exception.getMessage());
            verify(tokenHashService, times(1)).hash(rawCode);
            verify(userRepository, times(1)).findByEmail(email);
            verify(codeRepository, times(1)).findActiveByUserId(userId);
            verify(mockResetCode, times(1)).validate(hashedCode);
            verify(codeRepository, times(1)).save(mockResetCode);
        }
    }
}
