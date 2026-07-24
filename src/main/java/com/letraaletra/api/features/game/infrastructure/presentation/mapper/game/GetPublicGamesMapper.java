package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GetPublicGamesMapper {
    public static GetPublicGamesInput toInput(Pageable pageable) {
        return new GetPublicGamesInput(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort()
        );
    }

    public static PageResponse<GameDTO> toResponse(GetPublicGamesOutput output) {
        Page<Game> page = output.games();

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(GameDTOMapper::toDTO)
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
