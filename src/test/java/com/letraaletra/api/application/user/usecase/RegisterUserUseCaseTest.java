package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.application.user.service.PasswordService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.domain.user.exceptions.NicknameAlreadyInUseException;
import com.letraaletra.api.presentation.dto.mappers.UserDTOMapper;
import com.letraaletra.api.presentation.dto.response.user.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDTOMapper userDTOMapper;

    @InjectMocks
    private RegisterUserUseCase registerUser;

    @Test
    @DisplayName("Should register an User")
    void execute() {
        String nickname = "test";
        String email = "email@email.com";
        String password = "12345678";

        User user = new User("id", nickname, "avatar1", email, "hash");
        UserDTO expectedDTO = new UserDTO("id", nickname, "avatar1");

        Mockito.when(userRepository.existsByNickname(nickname))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(false);

        Mockito.when(passwordService.hash(password))
                .thenReturn("hash");

        Mockito.when(userDTOMapper.toDTO(Mockito.any(User.class)))
                .thenReturn(expectedDTO);

        UserDTO userDTO = registerUser.execute(nickname, email, password);

        Assertions.assertEquals("id", userDTO.id());
        Assertions.assertEquals(nickname, userDTO.nickname());
        Mockito.verify(passwordService).hash(password);
    }

    @Test
    @DisplayName("Should throw a NicknameAlreadyInUseException")
    void notRegisterBecauseNickname() {
        String nickname = "test";
        String email = "email@email.com";
        String password = "12345678";

        User user = new User("id", nickname, "avatar1", email, "hash");

        Mockito.when(userRepository.existsByNickname(nickname))
                .thenReturn(true);

        Assertions.assertThrows(NicknameAlreadyInUseException.class, () -> registerUser.execute(nickname, email, password));
    }

    @Test
    @DisplayName("Should throw a EmailAlreadyInUseException")
    void notRegisterBecauseEmail() {
        String nickname = "test";
        String email = "email@email.com";
        String password = "12345678";

        User user = new User("id", nickname, "avatar1", email, "hash");

        Mockito.when(userRepository.existsByNickname(nickname))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(true);

        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> registerUser.execute(nickname, email, password));
    }
}