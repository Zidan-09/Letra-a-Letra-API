package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.features.offers.domain.rewards.Reward;

import java.util.UUID;

public record OfferReward(UUID offerRewardId, Reward reward) {
}