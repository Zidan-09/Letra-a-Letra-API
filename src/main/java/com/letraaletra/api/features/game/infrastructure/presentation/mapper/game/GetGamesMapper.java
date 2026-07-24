package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class GetGamesMapper {
    public static GetGamesInput toInput(UUID auth, Pageable pageable) {
        return new GetGamesInput(
            auth,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort()
        );
    }

    public static PageResponse<GameHistory> toResponse(GetGamesOutput output) {
        Page<GameHistory> page = output.games();

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
