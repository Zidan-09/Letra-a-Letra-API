package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class LocalStorageGateway implements AssetStorageGateway {
    private final Logger logger = LoggerFactory.getLogger(LocalStorageGateway.class);

    @Override
    public String upload(byte[] file, String fileName, CosmeticTypes cosmeticType) {
        try {
            return cosmeticType.name() + "/" + fileName + ".webp";

        } catch (Exception e) {
            logger.error("Error on upload asset to CDN");

            throw e;
        }
    }

    @Override
    public String copy(String oldPath, String newName, CosmeticTypes newType) {
        try {
            return newType + "/" + newName + ".webp";
        } catch (Exception e) {
            logger.error("Error to move asset on CDN");

            throw e;
        }
    }

    @Override
    public void delete(String assetPath) {

    }
}
