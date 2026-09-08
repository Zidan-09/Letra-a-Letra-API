package com.letraaletra.api.features.reward.domain.exception;

import com.letraaletra.api.features.reward.domain.RewardMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidRewardQuantityException extends DomainException {
    public InvalidRewardQuantityException() {
        super(RewardMessages.INVALID_REWARD_QUANTITY);
    }
}
