package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
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
class AuthUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthUserUseCase authUserUseCase;

    private SignInInput input;
    private User user;
    private UUID userId;
    private int tokenVersion;

    @BeforeEach
    void setup() {
        input = new SignInInput("john@email.com", "123456");
        tokenVersion = 1;
        user = mock(User.class);
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should sign in successfully")
    void shouldSignInSuccessfully() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hashed-password");
        when(user.getUserId()).thenReturn(userId);
        when(user.getTokenVersion()).thenReturn(tokenVersion);

        when(passwordService.matches(input.password(), "hashed-password")).thenReturn(true);
        when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn("jwt-token");

        SignInOutput output = authUserUseCase.execute(input);

        assertNotNull(output);
        assertEquals(userId, output.id());
        assertEquals("jwt-token", output.token());

        verify(userRepository).findByEmail(input.email());
        verify(passwordService).matches("123456", "hashed-password");
        verify(user).incrementTokenVersion();
        verify(tokenService).generateUserToken(userId, tokenVersion);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authUserUseCase.execute(input));

        verify(userRepository).findByEmail(input.email());
        verifyNoInteractions(passwordService, tokenService);
    }

    @Test
    @DisplayName("should throw InvalidPasswordException when password is invalid")
    void shouldThrowWhenPasswordIsInvalid() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hashed-password");
        when(passwordService.matches(input.password(), "hashed-password")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> authUserUseCase.execute(input));

        verify(tokenService, never()).generateUserToken(any(), anyInt());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when password service fails")
    void shouldPropagatePasswordServiceException() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hashed-password");

        RuntimeException exception = new RuntimeException("password service error");
        when(passwordService.matches(input.password(), "hashed-password")).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> authUserUseCase.execute(input)
        );

        assertSame(exception, thrown);
        verify(tokenService, never()).generateUserToken(any(), anyInt());
    }

    @Test
    @DisplayName("should propagate exception when token generation fails")
    void shouldPropagateTokenGenerationException() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hashed-password");
        when(user.getUserId()).thenReturn(userId);
        when(user.getTokenVersion()).thenReturn(tokenVersion);
        when(passwordService.matches(input.password(), "hashed-password")).thenReturn(true);

        RuntimeException exception = new RuntimeException("token error");
        when(tokenService.generateUserToken(userId, tokenVersion)).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> authUserUseCase.execute(input)
        );

        assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should execute flow in correct order")
    void shouldExecuteFlowInCorrectOrder() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hash");
        when(user.getUserId()).thenReturn(userId);
        when(user.getTokenVersion()).thenReturn(tokenVersion);
        when(passwordService.matches(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateUserToken(any(), anyInt())).thenReturn("token");

        authUserUseCase.execute(input);

        InOrder inOrder = inOrder(userRepository, passwordService, user, tokenService);
        inOrder.verify(userRepository).findByEmail(input.email());
        inOrder.verify(passwordService).matches("123456", "hash");
        inOrder.verify(user).incrementTokenVersion();
        inOrder.verify(tokenService).generateUserToken(userId, tokenVersion);
        inOrder.verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should generate admin token when user is admin")
    void shouldGenerateAdminToken() {
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hashed-password");
        when(user.getUserId()).thenReturn(userId);
        when(user.getTokenVersion()).thenReturn(tokenVersion);
        when(passwordService.matches(input.password(), "hashed-password")).thenReturn(true);
        when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn("jwt-token");

        authUserUseCase.execute(input);

        verify(tokenService).generateUserToken(userId, tokenVersion);
        verify(userRepository).save(user);
    }
}