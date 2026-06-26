package com.letraaletra.api.features.cosmetic.application.port;

import org.springframework.web.multipart.MultipartFile;

public interface ImageConverter {
    byte[] convertToWebp(MultipartFile image);
}