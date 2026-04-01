package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.SignInCommand;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.infrastructure.security.BCryptPasswordService;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.InvalidPasswordException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCase {

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private BCryptPasswordService BCryptPasswordService;

    @Autowired
    private UserRepository userRepository;

    public SignInOutput login(SignInCommand command) {
        User user = userRepository.findByEmail(command.email());

        validateUser(user);

        checkMatch(command.password(), user.getPassword());

        String token = jsonWebTokenService.generateToken(user.getId());

        return buildReturn(user.getId(), token);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void checkMatch(String password, String hash) {
        boolean matches = BCryptPasswordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }

    private SignInOutput buildReturn(String userId, String token) {
        return new SignInOutput(
                userId,
                token
        );
    }
}
