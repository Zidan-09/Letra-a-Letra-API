package com.letraaletra.api.features.levels.infrastructure.config;

import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.usecase.CreateLevelUseCase;
import com.letraaletra.api.features.levels.application.usecase.FindLevelUseCase;
import com.letraaletra.api.features.levels.application.usecase.GetLevelsUseCase;
import com.letraaletra.api.features.levels.application.usecase.UpdateLevelUseCase;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LevelConfig {
    @Bean
    public CreateLevelUseCase createLevelUseCase(
            LevelRepository levelRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        return new CreateLevelUseCase(
                levelRepository,
                cosmeticRepository,
                adminChecker
        );
    }

    @Bean
    public GetLevelsUseCase getLevelsUseCase(
            LevelRepository levelRepository
    ) {
        return new GetLevelsUseCase(
                levelRepository
        );
    }

    @Bean
    public FindLevelUseCase findLevelUseCase(
            LevelRepository levelRepository
    ) {
        return new FindLevelUseCase(
                levelRepository
        );
    }

    @Bean
    public UpdateLevelUseCase updateLevelUseCase(
            LevelRepository levelRepository,
            CosmeticRepository cosmeticRepository,
            AdminChecker adminChecker
    ) {
        return new UpdateLevelUseCase(
                levelRepository,
                cosmeticRepository,
                adminChecker
        );
    }
}
