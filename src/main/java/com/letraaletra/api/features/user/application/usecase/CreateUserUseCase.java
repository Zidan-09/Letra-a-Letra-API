package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.springframework.transaction.annotation.Transactional;

public class CreateUserUseCase implements UseCase<CreateUserInput, CreateUserOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final NicknameService nicknameService;

    public CreateUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            NicknameService nicknameService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.nicknameService = nicknameService;
    }

    @Override
    @Transactional
    public CreateUserOutput execute(CreateUserInput input) {

        String email = input.email();
        String password = input.password();

        validateEmail(email);

        String nickname = nicknameService.get();

        String passwordHashed = passwordService.hash(password);

        User user = UserFactory.createLocal(nickname, email, passwordHashed);

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
                user
        );
    }
}
