package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.application.port.ImageConverter;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Objects;

@Component
public class WebpImageConverter implements ImageConverter {
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

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

            System.out.println(writers.hasNext());

            ImageOutputStream ios = ImageIO.createImageOutputStream(output);

//            writer.setOutput(ios);
//            writer.write(original);

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("failed_to_convert_image", e);
        }
    }
}
