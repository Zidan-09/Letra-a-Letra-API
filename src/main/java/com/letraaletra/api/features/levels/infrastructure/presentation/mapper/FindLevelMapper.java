package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.FindLevelResponse;

import java.util.UUID;

public class FindLevelMapper {
    public static FindLevelInput toInput(UUID levelId) {
        return new FindLevelInput(
                levelId
        );
    }

    public static FindLevelResponse toResponse(FindLevelOutput output) {
        return new FindLevelResponse(
                output.level()
        );
    }
}
