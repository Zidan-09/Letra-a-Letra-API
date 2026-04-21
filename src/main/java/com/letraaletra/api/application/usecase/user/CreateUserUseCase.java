package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.EmailAlreadyInUseException;

import java.util.Random;
import java.util.UUID;

public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final Random random;

    private static final String[] firstPart = {
            "Wolf", "Falcon", "Tiger", "Eagle", "Panther",
            "Dragon", "Viper", "Shadow", "Storm", "Blaze",
            "Frost", "Thunder", "Ghost", "Phantom", "Raven",
            "Hawk", "Lion", "Scorpion", "Kraken", "Phoenix",
            "Titan", "Hunter", "Sniper", "Warrior", "Knight",
            "Samurai", "Ninja", "Gladiator", "Raider", "Assassin",
            "Guardian", "Sentinel", "Nomad", "Warden", "Paladin",
            "Berserker"
    };

    private static final String[] secondPart = {
            "Attacker", "Slayer", "Breaker", "Striker", "Shooter",
            "Sniper", "Destroyer", "Crusher", "Invoker", "Caster",
            "Rider", "Walker", "Runner", "Jumper", "Seeker",
            "Tracker", "Hunter", "Defender", "Protector", "Conqueror",
            "Ruler", "Master", "Lord", "King", "Reaper",
            "Executioner", "Avenger", "Dominator", "Overlord", "Vanquisher"
    };

    public CreateUserUseCase(UserRepository userRepository, PasswordService passwordService, Random random) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.random = random;
    }

    public CreateUserOutput execute(CreateUserCommand command) {

        String email = command.email();
        String password = command.password();

        validateEmail(email);

        String userId = UUID.randomUUID().toString();

        String nickname = randomNicknameSelector();

        User user = new User(
                userId,
                nickname,
                null,
                email,
                passwordService.hash(password),
                null
        );

        try {
            userRepository.save(user);

        } catch (Exception e) {
            nickname = randomNicknameSelector();

            user.setNickname(nickname);

            userRepository.save(user);
        }

        return buildResult(user);
    }

    private void validateEmail(String email) {
        boolean existsEmail = userRepository.existsByEmail(email);

        if (existsEmail) {
            throw new EmailAlreadyInUseException();
        }
    }

    private String randomNicknameSelector() {
        String nickname = null;

        do {
            String first = firstPart[random.nextInt(firstPart.length)];
            String second = secondPart[random.nextInt(secondPart.length)];

            if (first.equalsIgnoreCase(second)) continue;

            int remainingLength = 15 - (first.length() + second.length());

            if (remainingLength < 2) continue;

            String suffix = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, remainingLength);

            nickname = first + second + suffix;

        } while (userRepository.existsByNickname(nickname));

        return nickname;
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
