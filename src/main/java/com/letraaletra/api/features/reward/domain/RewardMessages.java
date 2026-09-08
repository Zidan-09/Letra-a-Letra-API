package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum RewardMessages implements MessageCode {
    INVALID_REWARD_QUANTITY("the reward quantity must be greater than zero");

    private final String message;

    RewardMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
