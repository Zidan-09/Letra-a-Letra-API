package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.application.service.SelectNicknameService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.EmailAlreadyInUseException;
import com.letraaletra.api.features.user.domain.factory.UserFactory;

import java.util.ArrayList;
import java.util.List;

public class CreateUserUseCase implements UseCase<CreateUserInput, CreateUserOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserFactory userFactory;
    private final SelectNicknameService selectNicknameService;

    public CreateUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            UserFactory userFactory,
            SelectNicknameService selectNicknameService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.userFactory = userFactory;
        this.selectNicknameService = selectNicknameService;
    }

    public CreateUserOutput execute(CreateUserInput input) {

        String email = input.email();
        String password = input.password();

        validateEmail(email);

        String nickname = selectNicknameService.execute();

        User user = userFactory.createLocal(nickname, email, passwordService.hash(password));

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
                user.getEmail()
        );
    }
}
