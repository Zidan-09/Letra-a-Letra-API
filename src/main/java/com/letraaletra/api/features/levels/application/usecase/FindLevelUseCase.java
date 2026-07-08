package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindLevelUseCase implements UseCase<FindLevelInput, FindLevelOutput> {
    private final LevelRepository levelRepository;

    public FindLevelUseCase(
            LevelRepository levelRepository
    ) {
        this.levelRepository = levelRepository;
    }

    @Override
    public FindLevelOutput execute(FindLevelInput input) {
        Level level = levelRepository.find(input.levelId())
                .orElseThrow(LevelNotFoundException::new);

        return new FindLevelOutput(level);
    }
}
