package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.output.GetActiveGamesOutput;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GetActiveGamesMapper {
    public static GetActiveGamesInput toInput(AuthenticatedUser principal, Pageable pageable) {
        return new GetActiveGamesInput(
                principal,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static PageResponse<GameDTO> toResponse(GetActiveGamesOutput output) {
        Page<GameHistory> page = output.games();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(GameDTOMapper::toDTOFromHistory)
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
