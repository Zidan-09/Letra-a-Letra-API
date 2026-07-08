package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.GetLevelsResponse;
import org.springframework.data.domain.Pageable;

public class GetLevelsMapper {
    public static GetLevelsInput toInput(Pageable pageable) {
        return new GetLevelsInput(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static GetLevelsResponse toResponse(GetLevelsOutput output) {
        return new GetLevelsResponse(
                output.levels()
        );
    }
}
