package com.letraaletra.api.features.game.infrastructure.presentation.mapper.board;

import com.letraaletra.api.features.game.domain.board.word.Word;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word.WordResponse;

public class WordResponseMapper {
    public static WordResponse toResponse(Word word) {
        return new WordResponse(
                word.getValue(),
                word.isFound(),
                word.getFoundById() !=  null ? word.getFoundById().toString() : null
        );
    }
}
