package com.letraaletra.api.features.user.application.service;

import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

public class UpdateStatsService {
    private final UserRepository userRepository;

    public UpdateStatsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        user.getStats().incrementExperience(10);

        userRepository.save(user);
    }
}
