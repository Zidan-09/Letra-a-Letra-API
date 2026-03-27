package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.application.user.service.PasswordService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.domain.user.exceptions.NicknameAlreadyInUseException;
import com.letraaletra.api.presentation.dto.mappers.UserDTOMapper;
import com.letraaletra.api.presentation.dto.response.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserUseCase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserDTOMapper userDTOMapper;

    public UserDTO execute(String nickname, String email, String password) {
        validateNickname(nickname);

        validateEmail(email);

        String userId = UUID.randomUUID().toString();

        User user = new User(
                userId,
                nickname,
                "avatar id inventado",
                email,
                passwordService.hash(password)
        );

        userRepository.save(user);

        return userDTOMapper.toDTO(user);
    }

    private void validateNickname(String nickname) {
        boolean existsNickname = userRepository.existsByNickname(nickname);

        if (existsNickname) {
            throw new NicknameAlreadyInUseException();
        }
    }

    private void validateEmail(String email) {
        boolean existsEmail = userRepository.existsByEmail(email);

        if (existsEmail) {
            throw new EmailAlreadyInUseException();
        }
    }
}
