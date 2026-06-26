package com.letraaletra.api.features.cosmetic.application.port;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

public interface AssetStorageGateway {
    String upload(byte[] file, String fileName, CosmeticTypes cosmeticType);
}
