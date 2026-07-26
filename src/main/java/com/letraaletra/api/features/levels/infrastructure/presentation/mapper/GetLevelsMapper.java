package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GetLevelsMapper {
    public static GetLevelsInput toInput(Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetLevelsInput(
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<Level> toResponse(GetLevelsOutput output) {
        Page<Level> page = output.levels();

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
