package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.presentation.dto.mappers.UserDTOMapper;
import com.letraaletra.api.presentation.dto.response.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindUserByIdUseCase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOMapper userDTOMapper;

    public UserDTO execute(String id) {
        User user = userRepository.find(id);

        validateUser(user);

        return userDTOMapper.toDTO(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }
}
