package com.letraaletra.api.features.cosmetic.infrastructure.service;

import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.exceptions.ImageConversionException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.ImageTooLargeException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidImageTypeException;
import com.letraaletra.api.shared.domain.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

@Service
public class WebpImageConverter implements ImageConverter {

    @Override
    public byte[] convertToWebp(MultipartFile image) {
        if (image.getSize() > 5_000_000) {
            throw new ImageTooLargeException();
        }

        if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) {
            throw new InvalidImageTypeException();
        }

        try {
            BufferedImage original = ImageIO.read(image.getInputStream());

            if (original == null) {
                throw new InvalidCosmeticException();
            }

            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
                ImageWriter writer;

                try {
                    writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                } catch (Exception e) {
                    throw new IllegalStateException("No WebP ImageWriter is available.");
                }

                writer.setOutput(ios);
                writer.write(original);

                writer.dispose();
                ios.flush();

                return output.toByteArray();
            }
        } catch (Exception e) {
            if (e instanceof DomainException de) {
                throw de;
            };

            throw new ImageConversionException();
        }
    }
}
