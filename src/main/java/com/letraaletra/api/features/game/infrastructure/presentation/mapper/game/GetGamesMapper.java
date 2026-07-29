package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GetGamesMapper {
    public static GetGamesInput toInput(AuthenticatedUser principal, Pageable pageable) {
        return new GetGamesInput(
            principal,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort()
        );
    }

    public static PageResponse<GameResponse> toResponse(GetGamesOutput output) {
        Page<GameHistory> page = output.games();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(GameResponseMapper::toResponseFromHistory)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
