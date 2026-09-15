package com.letraaletra.api.features.inventory.infrastructure.service;

import com.letraaletra.api.features.inventory.application.port.ItemImageConverter;
import com.letraaletra.api.features.inventory.domain.exception.ImageConversionException;
import com.letraaletra.api.features.inventory.domain.exception.ImageTooLargeException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidImageTypeException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;
import com.letraaletra.api.shared.domain.DomainException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class WebpItemImageConverter implements ItemImageConverter {
    @Override
    public byte[] convertToWebp(byte[] content, String contentType) {
        if (content == null || content.length > 5_000_000) {
            throw new ImageTooLargeException();
        }

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidImageTypeException();
        }

        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(content));

            if (original == null) {
                throw new InvalidItemException();
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
            if (e instanceof DomainException domainException) {
                throw domainException;
            }

            throw new ImageConversionException();
        }
    }
}
