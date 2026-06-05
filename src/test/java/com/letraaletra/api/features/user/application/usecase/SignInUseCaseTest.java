package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.security.exceptions.InvalidPasswordException;
import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignInUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SignInUseCase signInUseCase;

    private SignInInput input;
    private User user;

    @BeforeEach
    void setup() {
        input = new SignInInput(
                "john@email.com",
                "123456"
        );

        user = mock(User.class);
    }

    @Test
    @DisplayName("should sign in successfully")
    void shouldSignInSuccessfully() {

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Optional.of(user));

        when(user.getHashPassword())
                .thenReturn("hashed-password");

        when(user.getId())
                .thenReturn("user-id");

        when(passwordService.matches(
                input.password(),
                "hashed-password"
        )).thenReturn(true);

        when(tokenService.generateToken("user-id"))
                .thenReturn("jwt-token");

        SignInOutput output = signInUseCase.execute(input);

        assertNotNull(output);
        assertEquals("user-id", output.id());
        assertEquals("jwt-token", output.token());

        verify(userRepository).findByEmail(input.email());
        verify(passwordService)
                .matches("123456", "hashed-password");
        verify(tokenService)
                .generateToken("user-id");
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserDoesNotExist() {

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> signInUseCase.execute(input)
        );

        verify(userRepository)
                .findByEmail(input.email());

        verifyNoInteractions(
                passwordService,
                tokenService
        );
    }

    @Test
    @DisplayName("should throw InvalidPasswordException when password is invalid")
    void shouldThrowWhenPasswordIsInvalid() {

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Optional.of(user));

        when(user.getHashPassword())
                .thenReturn("hashed-password");

        when(passwordService.matches(
                input.password(),
                "hashed-password"
        )).thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> signInUseCase.execute(input)
        );

        verify(tokenService, never())
                .generateToken(anyString());
    }

    @Test
    @DisplayName("should propagate exception when password service fails")
    void shouldPropagatePasswordServiceException() {

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Optional.of(user));

        when(user.getHashPassword())
                .thenReturn("hashed-password");

        RuntimeException exception =
                new RuntimeException("password service error");

        when(passwordService.matches(
                input.password(),
                "hashed-password"
        )).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> signInUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(tokenService, never())
                .generateToken(anyString());
    }

    @Test
    @DisplayName("should propagate exception when token generation fails")
    void shouldPropagateTokenGenerationException() {

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Optional.of(user));

        when(user.getHashPassword())
                .thenReturn("hashed-password");

        when(user.getId())
                .thenReturn("user-id");

        when(passwordService.matches(
                input.password(),
                "hashed-password"
        )).thenReturn(true);

        RuntimeException exception =
                new RuntimeException("token error");

        when(tokenService.generateToken("user-id"))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> signInUseCase.execute(input)
        );

        assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should execute flow in correct order")
    void shouldExecuteFlowInCorrectOrder() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(user.getHashPassword())
                .thenReturn("hash");

        when(user.getId())
                .thenReturn("user-id");

        when(passwordService.matches(anyString(), anyString()))
                .thenReturn(true);

        when(tokenService.generateToken(anyString()))
                .thenReturn("token");

        signInUseCase.execute(input);

        InOrder inOrder = inOrder(
                userRepository,
                passwordService,
                tokenService
        );

        inOrder.verify(userRepository)
                .findByEmail(input.email());

        inOrder.verify(passwordService)
                .matches("123456", "hash");

        inOrder.verify(tokenService)
                .generateToken("user-id");
    }
}