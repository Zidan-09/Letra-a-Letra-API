package com.letraaletra.api.shared.domain;

public class DomainException extends RuntimeException {

    private final MessageCode messageCode;

    public DomainException(MessageCode messageCode) {
        super(messageCode.getMessage());
        this.messageCode = messageCode;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }
}