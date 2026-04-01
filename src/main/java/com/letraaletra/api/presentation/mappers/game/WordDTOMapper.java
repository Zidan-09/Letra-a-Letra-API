package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.presentation.dto.response.game.WordDTO;
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
