package com.letraaletra.api.utils.theme;

import com.letraaletra.api.domain.theme.Theme;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
public class ThemeLoader {
    private final ObjectMapper objectMapper;

    public ThemeLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Theme> load() {
        try (InputStream inputStream =
                     getClass().getResourceAsStream("/data/themes.json")) {
            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Theme>>() {}
            );
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load themes", ex);
        }
    }
}
