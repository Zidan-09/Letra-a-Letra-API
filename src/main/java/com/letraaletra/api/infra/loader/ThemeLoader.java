package com.letraaletra.api.infra.loader;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.exception.messages.ThemeMessages;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ThemeLoader {
    private final ObjectMapper objectMapper;

    public ThemeLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Theme> load() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/themes.json")) {
            Map<String, List<String>> rawData = objectMapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, List<String>>>() {}
            );

            return rawData.entrySet().stream()
                    .map(entry -> new Theme(entry.getKey(), entry.getValue()))
                    .toList();

        } catch (Exception ex) {
            throw new RuntimeException(ThemeMessages.FAILED_TO_LOAD_THEMES.getMessage(), ex);
        }
    }
}
