package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ImageTooLargeException extends DomainException {
    public ImageTooLargeException() {
        super(ItemMessages.IMAGE_TOO_LARGE);
    }
}
