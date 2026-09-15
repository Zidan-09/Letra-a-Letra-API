package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ImageConversionException extends DomainException {
    public ImageConversionException() {
        super(ItemMessages.IMAGE_CONVERSION_FAILED);
    }
}
