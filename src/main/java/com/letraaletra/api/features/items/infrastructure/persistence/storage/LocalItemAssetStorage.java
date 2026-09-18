package com.letraaletra.api.features.items.infrastructure.persistence.storage;

import com.letraaletra.api.features.items.domain.EquippableCategory;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class LocalItemAssetStorage implements ItemAssetStorage {
    private final Logger logger = LoggerFactory.getLogger(LocalItemAssetStorage.class);

    @Override
    public String upload(byte[] file, String fileName, EquippableCategory category) {
        try {
            return category.name() + "/" + fileName + ".webp";
        } catch (Exception e) {
            logger.error("Error on upload asset to CDN");

            throw e;
        }
    }

    @Override
    public String copy(String oldPath, String newName, EquippableCategory newCategory) {
        try {
            return newCategory.name() + "/" + newName + ".webp";
        } catch (Exception e) {
            logger.error("Error to move asset on CDN");

            throw e;
        }
    }

    @Override
    public void delete(String assetPath) {
    }
}
