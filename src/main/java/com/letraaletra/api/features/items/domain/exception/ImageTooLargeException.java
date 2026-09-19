package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.PayloadTooLargeDomainException;

public class ImageTooLargeException extends PayloadTooLargeDomainException {
    public ImageTooLargeException() {
        super(ItemMessages.IMAGE_TOO_LARGE);
    }
}
