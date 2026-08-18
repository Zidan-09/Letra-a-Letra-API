package com.letraaletra.api.features.user.application.port;

import com.letraaletra.api.features.user.domain.User;

public interface UserStatsService {
    void update(User user, boolean isWinner);
}
