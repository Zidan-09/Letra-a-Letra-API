package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ImageTooLargeException extends DomainException {
    public ImageTooLargeException() {
        super(CosmeticMessages.IMAGE_TOO_LARGE);
    }
}
