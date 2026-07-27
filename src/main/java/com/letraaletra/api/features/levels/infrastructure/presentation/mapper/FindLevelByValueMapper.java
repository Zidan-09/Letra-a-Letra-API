package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.FindLevelByValueInput;
import com.letraaletra.api.features.levels.application.output.FindLevelByValueOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.FindLevelByValueResponse;

public class FindLevelByValueMapper {
    public static FindLevelByValueInput toInput(int value) {
        return new FindLevelByValueInput(
                value
        );
    }

    public static FindLevelByValueResponse toResponse(FindLevelByValueOutput output) {
        return new FindLevelByValueResponse(
                LevelResponseMapper.toResponse(output.level())
        );
    }
}
