package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;

import java.util.UUID;

public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public CreateUserUseCase(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public CreateUserOutput execute(CreateUserCommand command) {

        String email = command.email();
        String password = command.password();

        validateEmail(email);

        String userId = UUID.randomUUID().toString();

        User user = new User(
                userId,
                null,
                null,
                email,
                passwordService.hash(password),
                null
        );

        userRepository.save(user);

        return buildResult(user);
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
