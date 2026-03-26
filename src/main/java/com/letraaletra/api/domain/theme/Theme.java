package com.letraaletra.api.domain.theme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
}
