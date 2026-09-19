package com.letraaletra.api.features.reward.domain.exception;

import com.letraaletra.api.features.reward.domain.RewardMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidRewardQuantityException extends BadRequestDomainException {
    public InvalidRewardQuantityException() {
        super(RewardMessages.INVALID_REWARD_QUANTITY);
    }
}
