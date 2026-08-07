package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ImageConversionException extends DomainException {
    public ImageConversionException() {
        super(CosmeticMessages.IMAGE_CONVERSION_FAILED);
    }
}
