package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.service.SelectNicknameService;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserFactory userFactory;

    @Mock
    private SelectNicknameService selectNicknameService;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("should create an user correctly")
    void createUser() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(false);

        when(selectNicknameService.execute())
                .thenReturn("john123");

        when(passwordService.hash(input.password()))
                .thenReturn("hashed-password");

        User user = mock(User.class);

        when(userFactory.createLocal(
                anyString(),
                eq("john123"),
                eq(input.email()),
                eq("hashed-password")
        )).thenReturn(user);

        when(user.getId()).thenReturn("user-id");
        when(user.getNickname()).thenReturn("john123");
        when(user.getAvatar()).thenReturn("avatar.png");
        when(user.getEmail()).thenReturn("john@email.com");

        CreateUserOutput output = createUserUseCase.execute(input);

        assertNotNull(output);

        assertEquals("user-id", output.id());
        assertEquals("john123", output.nickname());
        assertEquals("avatar.png", output.avatar());
        assertEquals("john@email.com", output.email());

        verify(userRepository).existsByEmail("john@email.com");
        verify(selectNicknameService).execute();
        verify(passwordService).hash("123456");
        verify(userFactory).createLocal(
                anyString(),
                eq("john123"),
                eq("john@email.com"),
                eq("hashed-password")
        );
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw EmailAlreadyInUseException when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> createUserUseCase.execute(input)
        );

        verify(userRepository).existsByEmail(input.email());

        verifyNoInteractions(
                passwordService,
                userFactory,
                selectNicknameService
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when nickname service fails")
    void shouldPropagateNicknameException() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(false);

        RuntimeException exception = new RuntimeException("nickname error");

        when(selectNicknameService.execute())
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when password hashing fails")
    void shouldPropagateHashException() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(false);

        when(selectNicknameService.execute())
                .thenReturn("john123");

        RuntimeException exception = new RuntimeException("hash error");

        when(passwordService.hash(input.password()))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when user factory fails")
    void shouldPropagateFactoryException() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(false);

        when(selectNicknameService.execute())
                .thenReturn("john123");

        when(passwordService.hash(input.password()))
                .thenReturn("hashed-password");

        RuntimeException exception = new RuntimeException("factory error");

        when(userFactory.createLocal(
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when repository save fails")
    void shouldPropagateSaveException() {
        CreateUserInput input = new CreateUserInput(
                "john@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(input.email()))
                .thenReturn(false);

        when(selectNicknameService.execute())
                .thenReturn("john123");

        when(passwordService.hash(input.password()))
                .thenReturn("hashed-password");

        User user = mock(User.class);

        when(userFactory.createLocal(
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(user);

        RuntimeException exception = new RuntimeException("save error");

        doThrow(exception)
                .when(userRepository)
                .save(user);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);
    }
}