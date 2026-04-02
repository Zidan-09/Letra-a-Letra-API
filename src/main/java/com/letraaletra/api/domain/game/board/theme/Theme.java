package com.letraaletra.api.domain.game.board.theme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Theme {
    private final String id;
    private final String name;
    private final List<String> words;

    @JsonCreator
    public Theme(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("words") List<String> words
    ) {
        this.id = id;
        this.name = name;
        this.words = List.copyOf(words);
    }

    public String getThemeId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> pickRandomWords(int amount, Random random) {
        List<String> shuffled = new ArrayList<>(this.words);
        Collections.shuffle(shuffled, random);

        return shuffled.stream()
                .limit(amount)
                .toList();
    }
}
