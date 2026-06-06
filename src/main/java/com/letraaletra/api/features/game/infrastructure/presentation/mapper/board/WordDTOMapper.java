package com.letraaletra.api.features.game.infrastructure.presentation.mapper.board;

import com.letraaletra.api.features.game.domain.board.word.Word;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word.WordDTO;
import org.springframework.stereotype.Component;

@Component
public class WordDTOMapper {
    public WordDTO toDTO(Word word) {
        return new WordDTO(
                word.getValue(),
                word.isFound(),
                word.getFoundById()
        );
    }
}
