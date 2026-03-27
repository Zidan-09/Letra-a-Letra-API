package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    @InjectMocks
    private FindUserByIdUseCase findUserById;

    @Test
    @DisplayName("Should return an User from the id")
    void execute() {
        String id = "id";

        User user = new User("id", "teste", "avatar1", "email@email.com", "hash");

        UserDTO expectedDTO = new UserDTO(id, user.getNickname(), user.getAvatar());

        Mockito.when(userRepository.find(id))
                .thenReturn(user);

        Mockito.when(userDTOMapper.toDTO(user))
                .thenReturn(expectedDTO);

        UserDTO userDTO = findUserById.execute(id);

        Assertions.assertEquals(id, userDTO.id());
        Mockito.verify(userDTOMapper).toDTO(user);
    }

    @Test
    @DisplayName("Should throw a UserNotFoundException")
    void notExecute() {
        String id = "id";

        Mockito.when(userRepository.find(id))
                .thenReturn(null);

        Assertions.assertThrows(UserNotFoundException.class, () -> findUserById.execute(id));
    }
}