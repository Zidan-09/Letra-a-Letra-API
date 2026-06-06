package com.letraaletra.api.features.cosmetic.infrastructure.loader;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
public class CosmeticLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Cosmetic> load() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/cosmetic.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File /data/cosmetic.json not found!");
            }

            List<CosmeticJson> cosmetics = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {
                    }
            );

            return cosmetics.stream().map(cosmetic -> new Cosmetic(
                    cosmetic.id(),
                    cosmetic.name(),
                    cosmetic.type()
            ))
            .toList();

        } catch (Exception ex) {
            throw new RuntimeException(
                    CosmeticMessages.FAILED_TO_LOAD_COSMETICS.getMessage(),
                    ex
            );
        }
    }
}
