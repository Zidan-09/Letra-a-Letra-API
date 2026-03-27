package com.letraaletra.api.infra.loader;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.presentation.messages.ThemeMessages;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ThemeLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Theme> load() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/themes.json")) {

            List<Theme> themes = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {
                    }
            );

            return themes.stream()
                    .collect(Collectors.toMap(Theme::getThemeId, theme -> theme));

        } catch (Exception ex) {
            throw new RuntimeException(
                    ThemeMessages.FAILED_TO_LOAD_THEMES.getMessage(),
                    ex
            );
        }
    }
}