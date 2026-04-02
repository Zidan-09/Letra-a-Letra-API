package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.domain.user.exceptions.NicknameAlreadyInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateUserUseCase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    public CreateUserOutput execute(CreateUserCommand command) {

        String nickname = command.nickname();
        String email = command.email();
        String password = command.password();

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

        return buildResult(user);
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

    private CreateUserOutput buildResult(User user) {
        return new CreateUserOutput(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
