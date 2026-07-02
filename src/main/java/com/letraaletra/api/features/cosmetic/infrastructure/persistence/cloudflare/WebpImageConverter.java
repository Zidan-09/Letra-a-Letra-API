package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

@Component
public class WebpImageConverter implements ImageConverter {
    private final Logger logger = LoggerFactory.getLogger(WebpImageConverter.class);

    @Override
    public byte[] convertToWebp(MultipartFile image) {
        if (image.getSize() > 5_000_000) {
            throw new RuntimeException("image_too_large");
        }

        if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) {
            throw new RuntimeException("invalid_file_type");
        }

        try {
            BufferedImage original = ImageIO.read(image.getInputStream());

            if (original == null) {
                throw new InvalidCosmeticException();
            }

            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                 ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {

                ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

                if (writer == null) {
                    throw new RuntimeException("Writer not found");
                }

                writer.setOutput(ios);
                writer.write(original);

                writer.dispose();
                ios.flush();

                return output.toByteArray();
            }

        } catch (Exception e) {
            logger.error("Error to convert image to webp:", e);
            throw new RuntimeException("failed_to_convert_image", e);
        }
    }
}
