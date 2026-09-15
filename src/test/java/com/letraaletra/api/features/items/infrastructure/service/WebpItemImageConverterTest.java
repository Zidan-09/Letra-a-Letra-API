package com.letraaletra.api.features.items.infrastructure.service;

import com.letraaletra.api.features.items.domain.exception.ImageConversionException;
import com.letraaletra.api.features.items.domain.exception.ImageTooLargeException;
import com.letraaletra.api.features.items.domain.exception.InvalidImageTypeException;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebpItemImageConverterTest {

    @InjectMocks
    private WebpItemImageConverter converter;

    @Test
    @DisplayName("conteudo grande deve falhar com ImageTooLarge")
    void largeContentShouldFail() {
        byte[] content = new byte[5_000_001];

        ImageTooLargeException exception = assertThrows(
                ImageTooLargeException.class,
                () -> converter.convertToWebp(content, "image/png")
        );

        assertEquals("the image exceeds the maximum allowed size of 5 MB", exception.getMessage());
    }

    @Test
    @DisplayName("content type nao imagem deve falhar com InvalidImageType")
    void nonImageContentTypeShouldFail() {
        InvalidImageTypeException exception = assertThrows(
                InvalidImageTypeException.class,
                () -> converter.convertToWebp("hello".getBytes(), "text/plain")
        );

        assertEquals("the provided file is not a valid image", exception.getMessage());
    }

    @Test
    @DisplayName("bytes que nao sao imagem devem falhar")
    void nonImageBytesShouldFail() {
        assertThrows(
                InvalidItemException.class,
                () -> converter.convertToWebp("not an image".getBytes(), "image/png")
        );
    }

    @Test
    @DisplayName("falha de conversao deve virar ImageConversionFailed")
    void conversionFailureShouldMap() {
        byte[] valid1x1Png = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
                0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
                0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00,
                0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE,
                0x42, 0x60, (byte) 0x82
        };

        WebpItemImageConverter spyConverter = org.mockito.Mockito.spy(converter);

        try (org.mockito.MockedStatic<javax.imageio.ImageIO> mockedImageIO = org.mockito.Mockito.mockStatic(javax.imageio.ImageIO.class)) {
            mockedImageIO.when(() -> javax.imageio.ImageIO.read(org.mockito.ArgumentMatchers.any(java.io.InputStream.class)))
                    .thenCallRealMethod();

            mockedImageIO.when(() -> javax.imageio.ImageIO.createImageOutputStream(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new java.io.IOException("Error creating output stream"));

            ImageConversionException exception = assertThrows(
                    ImageConversionException.class,
                    () -> spyConverter.convertToWebp(valid1x1Png, "image/png")
            );

            assertEquals("failed to convert the image", exception.getMessage());
        }
    }
}
