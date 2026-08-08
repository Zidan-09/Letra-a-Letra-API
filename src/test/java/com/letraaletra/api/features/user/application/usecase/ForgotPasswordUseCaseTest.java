package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ForgotPasswordInput;
import com.letraaletra.api.features.user.application.port.PasswordResetCodeEmailService;
import com.letraaletra.api.features.user.application.port.ResetCodeService;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.reset.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordUseCase Unit Tests")
class ForgotPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private ResetCodeRepository codeRepository;

    @Mock
    private ResetCodeService resetCodeService;

    @Mock
    private PasswordResetCodeEmailService emailService;

    @InjectMocks
    private ForgotPasswordUseCase useCase;

    private String validEmail;
    private UUID userId;
    private String username;
    private User user;

    @BeforeEach
    void setUp() {
        validEmail = "usuario@teste.com";
        userId = UUID.randomUUID();
        username = "usuario_teste";

        user = mock(User.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve invalidar códigos anteriores, gerar e armazenar hash, criar PasswordResetCode e enviar e-mail com código plano")
        void execute_WhenUserExists_ShouldExecuteFullForgotPasswordFlow() {
            String rawCode = "123456";
            String hashedCode = "hashed_123456_token";

            ForgotPasswordInput input = new ForgotPasswordInput(validEmail);

            when(user.getUserId()).thenReturn(userId);
            when(user.getEmail()).thenReturn(validEmail);
            when(user.getUsername()).thenReturn(username);
            when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
            when(resetCodeService.generate()).thenReturn(rawCode);
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);

            PasswordResetCode mockResetCode = mock(PasswordResetCode.class);

            try (MockedStatic<PasswordResetCode> resetCodeStaticMock = mockStatic(PasswordResetCode.class)) {
                resetCodeStaticMock.when(() -> PasswordResetCode.create(userId, hashedCode))
                        .thenReturn(mockResetCode);

                Void result = useCase.execute(input);

                assertNull(result, "O retorno do usecase deve ser null");

                InOrder inOrder = inOrder(userRepository, codeRepository, resetCodeService, tokenHashService, emailService);
                inOrder.verify(userRepository).findByEmail(validEmail);
                inOrder.verify(codeRepository).invalidateAllByUserId(userId);
                inOrder.verify(resetCodeService).generate();
                inOrder.verify(tokenHashService).hash(rawCode);
                inOrder.verify(codeRepository).save(mockResetCode);
                inOrder.verify(emailService).send(validEmail, username, rawCode);

                resetCodeStaticMock.verify(() -> PasswordResetCode.create(userId, hashedCode), times(1));
                verifyNoMoreInteractions(userRepository, codeRepository, resetCodeService, tokenHashService, emailService);
            }
        }
    }

    @Nested
    @DisplayName("Proteção contra Enumeração de Usuários (User Not Found)")
    class UserNotFoundFlows {

        @Test
        @DisplayName("Deve retornar silenciosamente (null) sem disparar exceção ou ações secundárias quando o e-mail não existir")
        void execute_WhenUserDoesNotExist_ShouldReturnNullSilentlyWithoutInteractions() {
            String nonExistentEmail = "naoexistente@teste.com";
            ForgotPasswordInput input = new ForgotPasswordInput(nonExistentEmail);

            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            Void result = assertDoesNotThrow(() -> useCase.execute(input));

            assertNull(result, "Deve retornar null para nao revelar a inexistencia do usuario");
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
            verifyNoInteractions(codeRepository, resetCodeService, tokenHashService, emailService);
        }
    }

    @Nested
    @DisplayName("Regras de Domínio e Integridade")
    class DomainLogicAndInvariants {

        @Test
        @DisplayName("Deve interromper o fluxo sem salvar ou enviar e-mail se a criação estática de PasswordResetCode falhar")
        void execute_WhenPasswordResetCodeCreationFails_ShouldNotSaveOrSendEmail() {
            String rawCode = "654321";
            String hashedCode = "hashed_654321";

            ForgotPasswordInput input = new ForgotPasswordInput(validEmail);

            when(user.getUserId()).thenReturn(userId);
            when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
            when(resetCodeService.generate()).thenReturn(rawCode);
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);

            try (MockedStatic<PasswordResetCode> resetCodeStaticMock = mockStatic(PasswordResetCode.class)) {
                resetCodeStaticMock.when(() -> PasswordResetCode.create(userId, hashedCode))
                        .thenThrow(new IllegalArgumentException("Hash de codigo invalido"));

                IllegalArgumentException exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.execute(input)
                );

                assertEquals("Hash de codigo invalido", exception.getMessage());
                verify(codeRepository, times(1)).invalidateAllByUserId(userId);
                verify(codeRepository, never()).save(any());
                verifyNoInteractions(emailService);
            }
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Serviços e Infraestrutura")
    class ServiceFailures {

        @Test
        @DisplayName("Deve interromper a execução se o serviço de e-mail falhar após salvar o código de redefinição")
        void execute_WhenEmailServiceFails_ShouldPropagateException() {
            String rawCode = "999888";
            String hashedCode = "hashed_999888";

            ForgotPasswordInput input = new ForgotPasswordInput(validEmail);

            when(user.getUserId()).thenReturn(userId);
            when(user.getEmail()).thenReturn(validEmail);
            when(user.getUsername()).thenReturn(username);
            when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
            when(resetCodeService.generate()).thenReturn(rawCode);
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);

            PasswordResetCode mockResetCode = mock(PasswordResetCode.class);

            doThrow(new RuntimeException("Falha no servidor SMTP"))
                    .when(emailService).send(validEmail, username, rawCode);

            try (MockedStatic<PasswordResetCode> resetCodeStaticMock = mockStatic(PasswordResetCode.class)) {
                resetCodeStaticMock.when(() -> PasswordResetCode.create(userId, hashedCode))
                        .thenReturn(mockResetCode);

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> useCase.execute(input)
                );

                assertEquals("Falha no servidor SMTP", exception.getMessage());
                verify(codeRepository, times(1)).save(mockResetCode);
                verify(emailService, times(1)).send(validEmail, username, rawCode);
            }
        }

        @Test
        @DisplayName("Deve interromper a execução se o repositório falhar na invalidação prévia dos códigos")
        void execute_WhenCodeRepositoryInvalidateFails_ShouldPropagateExceptionAndNotGenerateCode() {
            ForgotPasswordInput input = new ForgotPasswordInput(validEmail);

            when(user.getUserId()).thenReturn(userId);
            when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));

            doThrow(new RuntimeException("Database timeout na invalidacao"))
                    .when(codeRepository).invalidateAllByUserId(userId);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Database timeout na invalidacao", exception.getMessage());
            verifyNoInteractions(resetCodeService, tokenHashService, emailService);
            verify(codeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {
        @Test
        @DisplayName("Deve buscar e retornar silenciosamente se o e-mail no input for nulo")
        void execute_WhenEmailInInputIsNull_ShouldQueryRepositoryWithNull() {
            ForgotPasswordInput nullEmailInput = new ForgotPasswordInput(null);

            when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

            Void result = useCase.execute(nullEmailInput);

            assertNull(result);
            verify(userRepository, times(1)).findByEmail(null);
            verifyNoInteractions(codeRepository, resetCodeService, tokenHashService, emailService);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "email_invalido_sem_arroba"})
        @DisplayName("Deve consultar repositório normalmente com e-mails vazios ou desformatados")
        void execute_WhenEmailIsMalformedOrBlank_ShouldQueryRepository(String rawEmail) {
            ForgotPasswordInput input = new ForgotPasswordInput(rawEmail);

            when(userRepository.findByEmail(rawEmail)).thenReturn(Optional.empty());

            Void result = useCase.execute(input);

            assertNull(result);
            verify(userRepository, times(1)).findByEmail(rawEmail);
            verifyNoInteractions(codeRepository, resetCodeService, tokenHashService, emailService);
        }
    }

    @Nested
    @DisplayName("Especificação de Comportamento e Segurança")
    class SecurityAndSpecificationTests {

        @Test
        @DisplayName("ESPECIFICAÇÃO DE SEGURANÇA: Garante que o código plano gerado NUNCA é salvo no repositório, apenas seu hash")
        void execute_ShouldEnsureRawCodeIsNotSavedInRepository() {
            String rawCode = "SECRET_123";
            String hashedCode = "HASHED_SECRET_123";

            ForgotPasswordInput input = new ForgotPasswordInput(validEmail);

            when(user.getUserId()).thenReturn(userId);
            when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
            when(resetCodeService.generate()).thenReturn(rawCode);
            when(tokenHashService.hash(rawCode)).thenReturn(hashedCode);

            try (MockedStatic<PasswordResetCode> resetCodeStaticMock = mockStatic(PasswordResetCode.class)) {
                PasswordResetCode mockResetCode = mock(PasswordResetCode.class);

                resetCodeStaticMock.when(() -> PasswordResetCode.create(userId, hashedCode))
                        .thenReturn(mockResetCode);

                useCase.execute(input);

                resetCodeStaticMock.verify(() -> PasswordResetCode.create(userId, rawCode), never());
                resetCodeStaticMock.verify(() -> PasswordResetCode.create(userId, hashedCode), times(1));
            }
        }
    }
}