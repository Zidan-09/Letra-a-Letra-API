package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserCannotChangeNicknameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ChangeNicknameUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangeNicknameUseCase changeNicknameUseCase;

    private UUID userId;
    private ChangeNicknameInput input;
    private User user;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        input = new ChangeNicknameInput(
                userId,
                "new-nickname"
        );

        user = mock(User.class);
    }

    @Test
    @DisplayName("should update nickname successfully")
    void shouldUpdateNicknameSuccessfully() {

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.canChangeNickname())
                .thenReturn(true);

        when(user.getNickname())
                .thenReturn("new-nickname");

        when(userRepository.existsByNickname("new-nickname"))
                .thenReturn(false);

        ChangeNicknameOutput output =
                changeNicknameUseCase.execute(input);

        assertEquals("new-nickname", output.user().getNickname());

        verify(user).setNickname("new-nickname");
        verify(user).setCanChangeNickname(false);

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserDoesNotExist() {

        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .existsByNickname(anyString());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("should throw NicknameAlreadyInUseException when nickname already exists")
    void shouldThrowWhenNicknameAlreadyExists() {

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.canChangeNickname())
                .thenReturn(true);

        when(userRepository.existsByNickname("new-nickname"))
                .thenReturn(true);

        assertThrows(
                NicknameAlreadyInUseException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .save(any());

        verify(user, never())
                .setNickname(anyString());
    }

    @Test
    @DisplayName("should throw UserCannotChangeNicknameException when user cannot change nickname")
    void shouldThrowWhenUserCannotChangeNickname() {

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.canChangeNickname())
                .thenReturn(false);

        assertThrows(
                UserCannotChangeNicknameException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .save(any());

        verify(user, never())
                .setNickname(anyString());
    }

    @Test
    @DisplayName("should propagate exception when nickname validation fails")
    void shouldPropagateExceptionFromExistsNickname() {

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        RuntimeException exception =
                new RuntimeException("database error");

        when(user.canChangeNickname())
                .thenReturn(true);

        when(userRepository.existsByNickname("new-nickname"))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("should propagate exception when save fails")
    void shouldPropagateSaveException() {

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByNickname("new-nickname"))
                .thenReturn(false);

        when(user.canChangeNickname())
                .thenReturn(true);

        RuntimeException exception =
                new RuntimeException("save error");

        doThrow(exception)
                .when(userRepository)
                .save(user);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        assertSame(exception, thrown);
    }
}