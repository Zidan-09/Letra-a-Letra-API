package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelsPage;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetLevelsUseCase implements UseCase<GetLevelsInput, GetLevelsOutput> {
    private final LevelRepository levelRepository;

    public GetLevelsUseCase(
            LevelRepository levelRepository
    ) {
        this.levelRepository = levelRepository;
    }

    @Override
    public GetLevelsOutput execute(GetLevelsInput input) {
        Page<Level> levels = levelRepository.get(
                new LevelsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new GetLevelsOutput(levels);
    }
}
