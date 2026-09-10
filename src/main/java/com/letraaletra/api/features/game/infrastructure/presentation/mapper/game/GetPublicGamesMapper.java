package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class GetPublicGamesMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("roomName", "roomCode", "status");

    public static GetPublicGamesInput toInput(Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new GetPublicGamesInput(
            pages.getPageNumber(),
            pages.getPageSize(),
            pages.getSort()
        );
    }

    public static PageResponse<GameResponse> toResponse(GetPublicGamesOutput output) {
        Page<Game> page = output.games();

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(GameResponseMapper::toResponse)
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
