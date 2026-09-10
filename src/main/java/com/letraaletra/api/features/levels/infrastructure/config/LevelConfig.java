package com.letraaletra.api.features.levels.infrastructure.config;

import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.FindLevelByValueInput;
import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.application.output.FindLevelByValueOutput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.application.usecase.*;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LevelConfig {
    @Bean
    public UseCase<CreateLevelInput, CreateLevelOutput> createLevelUseCase(
            LevelRepository levelRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateLevelUseCase(
                        levelRepository,
                        adminChecker,
                        rewardFactory
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetLevelsInput, GetLevelsOutput> getLevelsUseCase(
            LevelRepository levelRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetLevelsUseCase(
                        levelRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindLevelInput, FindLevelOutput> findLevelUseCase(
            LevelRepository levelRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindLevelUseCase(
                        levelRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UpdateLevelInput, UpdateLevelOutput> updateLevelUseCase(
            LevelRepository levelRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UpdateLevelUseCase(
                        levelRepository,
                        cosmeticRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindLevelByValueInput, FindLevelByValueOutput> findLevelByValueUseCase(
            LevelRepository levelRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindLevelByValueUseCase(
                        levelRepository
                ),
                transactions
        );
    }
}
