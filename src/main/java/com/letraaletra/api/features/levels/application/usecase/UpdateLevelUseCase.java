package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.exception.LevelAlreadyExistsException;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;

import java.util.List;
import java.util.UUID;

public class UpdateLevelUseCase implements UseCase<UpdateLevelInput, UpdateLevelOutput> {
    private final LevelRepository levelRepository;
    private final ItemRepository itemRepository;
    private final AdminChecker adminChecker;

    public UpdateLevelUseCase(
            LevelRepository levelRepository,
            ItemRepository itemRepository,
            AdminChecker adminChecker
    ) {
        this.levelRepository = levelRepository;
        this.itemRepository = itemRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateLevelOutput execute(UpdateLevelInput input) {
        adminChecker.check(input.principal(), PermissionKey.LEVELS, PermissionAction.EDIT);

        Level level = levelRepository.find(input.levelId())
                .orElseThrow(LevelNotFoundException::new);

        Level levelToCheck = levelRepository.findByLevel(input.level())
                .orElse(null);

        checkLevel(levelToCheck, input.levelId());

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

            case ITEM -> {
                Item item = itemRepository.findById(reward.rewardReference())
                        .orElseThrow(ItemNotFoundException::new);

                yield new LevelReward(
                        id,
                        new ItemGrantReward(item.getId(), reward.quantity())
                );
            }
        };
    }

    private void checkLevel(Level level, UUID levelId) {
        if (level != null && !level.getLevelId().equals(levelId))
            throw new LevelAlreadyExistsException();
    }
}
