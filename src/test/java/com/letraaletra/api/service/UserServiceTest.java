package com.letraaletra.api.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.response.user.UserDTO;
import com.letraaletra.api.exception.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.exception.exceptions.NicknameAlreadyInUseException;
import com.letraaletra.api.exception.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.repository.UserRepository;
import com.letraaletra.api.service.mappers.UserDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register user successfully")
    void create() {
        Mockito.when(userRepository.existsByNickname("Samuel"))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail("email@email.com"))
                .thenReturn(false);

        Mockito.when(passwordService.hash("12345678"))
                .thenReturn("hashed");

        Mockito.when(userDTOMapper.toDTO(Mockito.any()))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return new UserDTO(user.getId(), user.getNickname(), user.getAvatar());
                });

        UserDTO user = userService.create("Samuel", "email@email.com", "12345678");

        Assertions.assertEquals("Samuel", user.nickname());
    }

    @Test
    @DisplayName("Should throw NicknameAlreadyInUseException")
    void notCreateBecauseNickname() {
        Mockito.when(userRepository.existsByNickname("Samuel"))
                .thenReturn(true);

        Assertions.assertThrows(NicknameAlreadyInUseException.class, () -> userService.create("Samuel", "email@email.com", "12345678"));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException")
    void notCreateBecauseEmail() {
        Mockito.when(userRepository.existsByNickname("Samuel"))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail("email@email.com"))
                .thenReturn(true);

        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> userService.create("Samuel", "email@email.com", "12345678"));
    }

    @Test
    @DisplayName("Should return an User from the repository")
    void find() {
        String id = "123";

        Mockito.when(userRepository.find(id))
                .thenReturn(new User(id, "Samuel", "avatar", "email", "hashed"));

        Mockito.when(userDTOMapper.toDTO(Mockito.any()))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return new UserDTO(user.getId(), user.getNickname(), user.getAvatar());
                });

        UserDTO user = userService.find(id);

        Assertions.assertEquals(id, user.id());
    }

    @Test
    @DisplayName("Should throw an UserNotFoundException")
    void notFind() {
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.find("anything"));
    }

    @Test
    @DisplayName("Should return all Users from repository")
    void get() {
        List<User> users = List.of(
                new User("id", "Samuel", "avatar", "email@email.com", "hashed")
        );

        Mockito.when(userRepository.get()).thenReturn(users);

        List<User> result = userService.get();

        Assertions.assertEquals("Samuel", result.getFirst().getNickname());
    }

    @Test
    void createShouldSaveCorrectUser() {
        Mockito.when(userRepository.existsByNickname("Samuel")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("email@email.com")).thenReturn(false);
        Mockito.when(passwordService.hash("123")).thenReturn("hashed");

        Mockito.when(userDTOMapper.toDTO(Mockito.any()))
                .thenReturn(new UserDTO("id", "Samuel", "avatar"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        userService.create("Samuel", "email@email.com", "123");

        Mockito.verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        Assertions.assertEquals("Samuel", savedUser.getNickname());
        Assertions.assertEquals("hashed", savedUser.getPassword());
    }

    @Test
    @DisplayName("Should hash the user password")
    void passwordShouldBeHashed() {
        Mockito.when(userRepository.existsByNickname("Samuel")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("email@email.com")).thenReturn(false);
        Mockito.when(passwordService.hash("123")).thenReturn("hashed");

        Mockito.when(userDTOMapper.toDTO(Mockito.any()))
                .thenReturn(new UserDTO("id", "Samuel", "avatar"));

        userService.create("Samuel", "email@email.com", "123");

        Mockito.verify(passwordService).hash("123");
    }
}