package com.letraaletra.api.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.response.user.UserDTO;
import com.letraaletra.api.exception.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.repository.UserRepository;
import com.letraaletra.api.service.mappers.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private PasswordService passwordService;

    public UserDTO create(String nickname, String avatar, String email, String password) {
        String userId = UUID.randomUUID().toString();

        User user = new User(
                userId,
                nickname,
                avatar,
                email,
                passwordService.hash(password)
        );

        userRepository.save(user);

        return userDTOMapper.toDTO(user);
    }

    public UserDTO find(String id) {
        User user = userRepository.find(id);

        if (user == null) {
            throw new UserNotFoundException();
        }

        return userDTOMapper.toDTO(user);
    }

    public List<User> get() {
        return userRepository.get();
    }
}
