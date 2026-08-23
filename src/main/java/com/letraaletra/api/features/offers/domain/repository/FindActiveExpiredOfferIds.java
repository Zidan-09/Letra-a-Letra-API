package com.letraaletra.api.features.offers.domain.repository;

import java.util.List;
import java.util.UUID;

public interface FindActiveExpiredOfferIds {
    List<UUID> findActiveExpiredIds();
}
