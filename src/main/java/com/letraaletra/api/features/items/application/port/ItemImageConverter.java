package com.letraaletra.api.features.items.application.port;

public interface ItemImageConverter {
    byte[] convertToWebp(byte[] content, String contentType);
}
