package com.letraaletra.api.features.cosmetic.infrastructure.service;

import com.letraaletra.api.features.cosmetic.domain.exceptions.ImageConversionException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.ImageTooLargeException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidImageTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebpImageConverterTest {

    @InjectMocks
    private WebpImageConverter webpImageConverter;

    @Test
    @DisplayName("should throws a ImageTooLargeException when input is too large")
    void throwRuntimeExceptionImage() {
        byte[] content = new byte[5_000_001];

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                content
        );

        ImageTooLargeException exception = assertThrows(
                ImageTooLargeException.class,
                () -> webpImageConverter.convertToWebp(image)
        );

        assertEquals("the image exceeds the maximum allowed size of 5 MB", exception.getMessage());
    }

    @Test
    @DisplayName("should throws a InvalidImageTypeException when input is not an image")
    void throwInvalidInput() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        InvalidImageTypeException exception = assertThrows(
                InvalidImageTypeException.class,
                () -> webpImageConverter.convertToWebp(file)
        );

        assertEquals("the provided file is not a valid image", exception.getMessage());
    }

    @Test
    @DisplayName("should throws an InvalidCosmeticException when image readded is null")
    void throwsInvalidCosmeticException() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                "not an image".getBytes()
        );

        InvalidCosmeticException exception = assertThrows(
                InvalidCosmeticException.class,
                () -> webpImageConverter.convertToWebp(image)
        );

        assertEquals("the selected cosmetic is invalid", exception.getMessage());
    }

    @Test
    @DisplayName("should throws an ImageConversionException when the convert fail")
    void throwsRuntimeException() {
        byte[] valid1x1Png = new byte[] {
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

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                valid1x1Png
        );

        WebpImageConverter spyConverter = org.mockito.Mockito.spy(webpImageConverter);

        try (org.mockito.MockedStatic<javax.imageio.ImageIO> mockedImageIO = org.mockito.Mockito.mockStatic(javax.imageio.ImageIO.class)) {
            mockedImageIO.when(() -> javax.imageio.ImageIO.read(org.mockito.ArgumentMatchers.any(java.io.InputStream.class)))
                    .thenCallRealMethod();

            mockedImageIO.when(() -> javax.imageio.ImageIO.createImageOutputStream(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new java.io.IOException("Error creating output stream"));

            ImageConversionException exception = assertThrows(
                    ImageConversionException.class,
                    () -> spyConverter.convertToWebp(image)
            );

            assertEquals("failed to convert the image", exception.getMessage());
        }
    }
}