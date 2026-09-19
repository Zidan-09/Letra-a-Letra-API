package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class ImageConversionException extends BadRequestDomainException {
    public ImageConversionException() {
        super(ItemMessages.IMAGE_CONVERSION_FAILED);
    }
}
