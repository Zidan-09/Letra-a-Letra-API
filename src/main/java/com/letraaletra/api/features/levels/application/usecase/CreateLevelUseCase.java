package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.exception.LevelAlreadyExistsException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class CreateLevelUseCase implements UseCase<CreateLevelInput, CreateLevelOutput> {
    private final LevelRepository levelRepository;
    private final AdminChecker adminChecker;
    private final RewardFactory rewardFactory;

    public CreateLevelUseCase(
            LevelRepository levelRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory
    ) {
        this.levelRepository = levelRepository;
        this.adminChecker = adminChecker;
        this.rewardFactory = rewardFactory;
    }

    @Override
    @Transactional
    public CreateLevelOutput execute(CreateLevelInput input) {
        adminChecker.check(input.principal(), PermissionKey.LEVELS, PermissionAction.CREATE);

        if (levelRepository.existsByLevel(input.level())) throw new LevelAlreadyExistsException();

        Level level = buildLevel(input);

        levelRepository.save(level);

        return new CreateLevelOutput(level);
    }

    private Level buildLevel(CreateLevelInput input) {
        return Level.create(
                input.level(),
                buildRewards(input.rewards())
        );
    }

    private List<LevelReward> buildRewards(List<CreateLevelRewardInput> createLevelRewardInputs) {
        return createLevelRewardInputs.stream()
                .map(this::buildReward)
                .toList();
    }

    private LevelReward buildReward(CreateLevelRewardInput reward) {
        UUID id = UUID.randomUUID();

        return new LevelReward(
                id,
                rewardFactory.create(
                        reward.rewardType(),
                        reward.quantity(),
                        reward.rewardReference()
                )
        );
    }
}
