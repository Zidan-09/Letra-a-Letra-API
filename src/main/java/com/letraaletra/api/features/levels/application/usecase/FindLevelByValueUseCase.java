package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.FindLevelByValueInput;
import com.letraaletra.api.features.levels.application.output.FindLevelByValueOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindLevelByValueUseCase implements UseCase<FindLevelByValueInput, FindLevelByValueOutput> {
    private final LevelRepository levelRepository;

    public FindLevelByValueUseCase(
            LevelRepository levelRepository
    ) {
        this.levelRepository = levelRepository;
    }

    @Override
    public FindLevelByValueOutput execute(FindLevelByValueInput input) {
        Level level = levelRepository.findByLevel(input.value())
                .orElseThrow(LevelNotFoundException::new);

        return new FindLevelByValueOutput(level);
    }
}
