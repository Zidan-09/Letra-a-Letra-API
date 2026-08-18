package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleAuthUseCase Unit Tests")
class GoogleAuthUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private NicknameService nicknameService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoogleTokenService googleTokenService;

    @InjectMocks
    private GoogleAuthUseCase useCase;

    private String validGoogleToken;
    private String googleId;
    private String email;
    private UUID userId;
    private int tokenVersion;
    private String jwtToken;
    private GoogleAuthData googleAuthData;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validGoogleToken = "google.jwt.raw_token";
        googleId = "google_user_id_123456";
        email = "usuario.google@teste.com";
        userId = UUID.randomUUID();
        jwtToken = "generated.app.jwt_token";
        tokenVersion = 1;

        googleAuthData = new GoogleAuthData(email, googleId);
        mockUser = mock(User.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve autenticar usuário existente no banco e retornar o SignInOutput com token do sistema")
        void execute_WhenUserAlreadyExists_ShouldReturnSignInOutputWithoutCreatingNewUser() {
            AuthInput input = new AuthInput(validGoogleToken);

            when(googleTokenService.verify(validGoogleToken)).thenReturn(googleAuthData);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getUserId()).thenReturn(userId);
            when(mockUser.getTokenVersion()).thenReturn(tokenVersion);
            when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn(jwtToken);

            SignInOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(userId, output.id());
            assertEquals(jwtToken, output.token());

            InOrder inOrder = inOrder(googleTokenService, userRepository, mockUser, tokenService);
            inOrder.verify(googleTokenService).verify(validGoogleToken);
            inOrder.verify(userRepository).findByGoogleId(googleId);
            inOrder.verify(mockUser).incrementTokenVersion();
            inOrder.verify(tokenService).generateUserToken(userId, tokenVersion);
            inOrder.verify(userRepository).save(mockUser);

            verifyNoInteractions(nicknameService);
        }

        @Test
        @DisplayName("Deve registrar novo usuário via UserFactory ao não encontrar por Google ID e retornar SignInOutput")
        void execute_WhenUserDoesNotExist_ShouldCreateNewUserAndReturnSignInOutput() {
            String generatedNickname = "WolfStriker12a3";
            AuthInput input = new AuthInput(validGoogleToken);
            User createdUser = mock(User.class);

            when(googleTokenService.verify(validGoogleToken)).thenReturn(googleAuthData);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
            when(nicknameService.get()).thenReturn(generatedNickname);
            when(createdUser.getUserId()).thenReturn(userId);
            when(createdUser.getTokenVersion()).thenReturn(tokenVersion);
            when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn(jwtToken);

            try (MockedStatic<UserFactory> userFactoryMock = mockStatic(UserFactory.class)) {
                userFactoryMock.when(() -> UserFactory.createGoogle(generatedNickname, email, googleId))
                        .thenReturn(createdUser);

                SignInOutput output = useCase.execute(input);

                assertNotNull(output);
                assertEquals(userId, output.id());
                assertEquals(jwtToken, output.token());

                InOrder inOrder = inOrder(googleTokenService, userRepository, nicknameService, createdUser, tokenService);
                inOrder.verify(googleTokenService).verify(validGoogleToken);
                inOrder.verify(userRepository).findByGoogleId(googleId);
                inOrder.verify(nicknameService).get();
                inOrder.verify(createdUser).incrementTokenVersion();
                inOrder.verify(tokenService).generateUserToken(userId, tokenVersion);
                inOrder.verify(userRepository).save(createdUser);

                userFactoryMock.verify(() -> UserFactory.createGoogle(generatedNickname, email, googleId), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Validação de Token e Exceções do GoogleTokenService")
    class GoogleTokenValidationFailures {

        @Test
        @DisplayName("Deve interromper o fluxo e lançar InvalidTokenException quando o token do Google for inválido")
        void execute_WhenGoogleTokenIsInvalid_ShouldThrowInvalidTokenException() {
            String invalidToken = "invalid_token";
            AuthInput input = new AuthInput(invalidToken);

            doThrow(new InvalidTokenException())
                    .when(googleTokenService).verify(invalidToken);

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(input)
            );

            verify(googleTokenService, times(1)).verify(invalidToken);
            verifyNoInteractions(userRepository, nicknameService, tokenService);
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório e Serviços")
    class InfrastructureFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o NicknameService falhar na geração de apelido para novo usuário")
        void execute_WhenNicknameServiceFails_ShouldPropagateExceptionAndNotSaveUser() {
            AuthInput input = new AuthInput(validGoogleToken);

            when(googleTokenService.verify(validGoogleToken)).thenReturn(googleAuthData);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
            doThrow(new RuntimeException("Falha ao gerar nickname único"))
                    .when(nicknameService).get();

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Falha ao gerar nickname único", exception.getMessage());
            verify(googleTokenService, times(1)).verify(validGoogleToken);
            verify(userRepository, times(1)).findByGoogleId(googleId);
            verify(nicknameService, times(1)).get();
            verify(userRepository, never()).save(any());
            verifyNoInteractions(tokenService);
        }

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar o usuário no final do fluxo")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            String generatedNickname = "FalconHunter99a";
            AuthInput input = new AuthInput(validGoogleToken);
            User createdUser = mock(User.class);

            when(googleTokenService.verify(validGoogleToken)).thenReturn(googleAuthData);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
            when(nicknameService.get()).thenReturn(generatedNickname);
            when(createdUser.getUserId()).thenReturn(userId);
            when(createdUser.getTokenVersion()).thenReturn(tokenVersion);
            when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn(jwtToken);

            doThrow(new RuntimeException("Erro de conexão com o banco de dados"))
                    .when(userRepository).save(any(User.class));

            try (MockedStatic<UserFactory> userFactoryMock = mockStatic(UserFactory.class)) {
                userFactoryMock.when(() -> UserFactory.createGoogle(generatedNickname, email, googleId))
                        .thenReturn(createdUser);

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> useCase.execute(input)
                );

                assertEquals("Erro de conexão com o banco de dados", exception.getMessage());
                verify(tokenService).generateUserToken(userId, tokenVersion);
                verify(userRepository, times(1)).save(createdUser);
            }
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar token nulo do input para o GoogleTokenService")
        void execute_WhenTokenInInputIsNull_ShouldPassNullToGoogleTokenService() {
            AuthInput nullTokenInput = new AuthInput(null);

            doThrow(new InvalidTokenException())
                    .when(googleTokenService).verify(null);

            assertThrows(
                    InvalidTokenException.class,
                    () -> useCase.execute(nullTokenInput)
            );

            verify(googleTokenService, times(1)).verify(null);
            verifyNoInteractions(userRepository, nicknameService, tokenService);
        }
    }
}