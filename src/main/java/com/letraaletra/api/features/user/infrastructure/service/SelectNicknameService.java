package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SelectNicknameService implements NicknameService {
    private final UserRepository userRepository;
    private final Random random = new Random();

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

    @Override
    public String get() {
        while (true) {

            String first = firstPart[random.nextInt(firstPart.length)];
            String second = secondPart[random.nextInt(secondPart.length)];

            if (first.equalsIgnoreCase(second)) continue;

            int remainingLength = 15 - (first.length() + second.length());

            if (remainingLength < 2) continue;

            String suffix = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, remainingLength);

            String nickname = first + second + suffix;

            if (!userRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }
    }
}
