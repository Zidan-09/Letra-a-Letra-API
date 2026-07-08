package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetLevelsUseCase implements UseCase<GetLevelsInput, GetLevelsOutput> {
    private final LevelRepository levelRepository;

    public GetLevelsUseCase(
            LevelRepository levelRepository
    ) {
        this.levelRepository = levelRepository;
    }

    @Override
    public GetLevelsOutput execute(GetLevelsInput input) {
        List<Level> levels = levelRepository.get(input);

        return new GetLevelsOutput(levels);
    }
}
