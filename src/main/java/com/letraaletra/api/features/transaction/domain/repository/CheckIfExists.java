package com.letraaletra.api.features.transaction.domain.repository;

import java.util.UUID;

public interface CheckIfExists {
    boolean existsOfferPurchase(UUID userId, UUID referenceId);
}
