package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
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
    @DisplayName("should throws a RuntimeException when input is too large")
    void throwRuntimeExceptionImage() {
        byte[] content = new byte[5_000_001];

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                content
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webpImageConverter.convertToWebp(image)
        );

        assertEquals("image_too_large", exception.getMessage());
    }

    @Test
    @DisplayName("should throws a RuntimeException when input is not an image")
    void throwInvalidInput() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webpImageConverter.convertToWebp(file)
        );

        assertEquals("invalid_file_type", exception.getMessage());
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

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webpImageConverter.convertToWebp(image)
        );

        assertEquals("failed_to_convert_image", exception.getMessage());
        assertInstanceOf(InvalidCosmeticException.class, exception.getCause());
    }

    @Test
    @DisplayName("should throws an RuntimeException when the convert fail")
    void throwsRuntimeException() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                new byte[0]
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webpImageConverter.convertToWebp(image)
        );

        assertEquals("failed_to_convert_image", exception.getMessage());
    }

}