package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;

import java.util.List;
import java.util.UUID;

public class UpdateLevelUseCase implements UseCase<UpdateLevelInput, UpdateLevelOutput> {
    private final LevelRepository levelRepository;
    private final CosmeticRepository cosmeticRepository;
    private final AdminChecker adminChecker;

    public UpdateLevelUseCase(
            LevelRepository levelRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        this.levelRepository = levelRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateLevelOutput execute(UpdateLevelInput input) {
        adminChecker.check(input.auth());

        Level level = levelRepository.find(input.levelId())
                .orElseThrow(LevelNotFoundException::new);

        level.setLevel(input.level());
        level.setRewards(buildRewards(input.rewards()));

        levelRepository.save(level);

        return new UpdateLevelOutput(level);
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
