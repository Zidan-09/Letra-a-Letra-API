package com.letraaletra.api.features.ranking.domain.repository;

import com.letraaletra.api.shared.domain.OnlineUser;

public interface EnqueueUser {
    void add(OnlineUser onlineUser);
}
