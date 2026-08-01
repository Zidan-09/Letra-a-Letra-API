package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.exception.LevelAlreadyExistsException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class CreateLevelUseCase implements UseCase<CreateLevelInput, CreateLevelOutput> {
    private final LevelRepository levelRepository;
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public CreateLevelUseCase(
            LevelRepository levelRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.levelRepository = levelRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
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

        return switch (reward.rewardType()) {
            case COIN -> new LevelReward(
                    id,
                    new SoftCoinsReward(reward.quantity())
            );

            case GEMS -> new LevelReward(
                    id,
                    new HardGemsReward(reward.quantity())
            );

            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticRepository.find(reward.rewardReference())
                        .orElseThrow(CosmeticNotFoundException::new);

                yield new LevelReward(
                        id,
                        new CosmeticReward(cosmetic)
                );
            }
        };
    }
}
