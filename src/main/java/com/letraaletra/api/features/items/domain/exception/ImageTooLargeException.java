package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ImageTooLargeException extends DomainException {
    public ImageTooLargeException() {
        super(ItemMessages.IMAGE_TOO_LARGE);
    }
}
