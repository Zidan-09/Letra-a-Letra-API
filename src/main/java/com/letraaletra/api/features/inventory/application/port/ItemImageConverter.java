package com.letraaletra.api.features.inventory.application.port;

public interface ItemImageConverter {
    byte[] convertToWebp(byte[] content, String contentType);
}
