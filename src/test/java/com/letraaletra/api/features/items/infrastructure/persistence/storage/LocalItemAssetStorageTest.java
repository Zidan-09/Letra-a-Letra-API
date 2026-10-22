package com.letraaletra.api.features.items.infrastructure.persistence.storage;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LocalItemAssetStorage Unit Tests")
class LocalItemAssetStorageTest {

    private final LocalItemAssetStorage storage = new LocalItemAssetStorage();

    @Test
    @DisplayName("upload deve gerar caminho de CDN com o slug do nome")
    void uploadShouldBuildCdnPathWithSlug() {
        String assetPath = storage.upload(new byte[]{1, 2, 3}, "Letra a Letra Banner", EquippableCategory.BANNER);

        assertEquals("BANNER/letra-a-letra-banner.webp", assetPath);
    }

    @Test
    @DisplayName("copy deve gerar caminho de CDN com o slug do novo nome")
    void copyShouldBuildCdnPathWithSlug() {
        String assetPath = storage.copy("BANNER/letra-a-letra-banner.webp", "Letra a Letra Banner", EquippableCategory.BANNER);

        assertEquals("BANNER/letra-a-letra-banner.webp", assetPath);
    }

    @Test
    @DisplayName("upload deve manter o nome sem alteracoes quando ele ja e um slug")
    void uploadShouldKeepAlreadySlugifiedName() {
        String assetPath = storage.upload(new byte[]{1, 2, 3}, "letra-a-letra-banner", EquippableCategory.BANNER);

        assertEquals("BANNER/letra-a-letra-banner.webp", assetPath);
    }
}
