package com.letraaletra.api.features.user.application.service;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

public class UpdateStatsService {
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;

    public UpdateStatsService(
            UserRepository userRepository,
            LevelRepository levelRepository
    ) {
        this.userRepository = userRepository;
        this.levelRepository = levelRepository;
    }

    public void execute(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        int maxLevel = levelRepository.findBiggestLevel();

        int experience = isWinner ? 10 * 3 : 10;

        int beforeLevel = user.getStats().getLevel();

        user.getStats().incrementExperience(experience, maxLevel);

        int afterLevel = user.getStats().getLevel();

        if (afterLevel > beforeLevel) {
            Level level = levelRepository.findByLevel(afterLevel)
                    .orElseThrow(LevelNotFoundException::new);

            level.getRewards().forEach(levelReward ->
                    levelReward.reward().deliver(user));
        }
    }
}
