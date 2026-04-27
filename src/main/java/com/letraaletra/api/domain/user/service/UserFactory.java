package com.letraaletra.api.domain.user.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.stats.UserStats;

public class UserFactory {
    public User createLocal(String id, String nickname, String email, String passwordHash) {
        return new User(
                id,
                nickname,
                null,
                email,
                passwordHash,
                null,
                true,
                getInitial()
        );
    }

    public User createGoogle(String id, String email, String googleId) {
        return new User(
                id,
                null,
                null,
                email,
                null,
                googleId,
                true,
                getInitial()
        );
    }

    private UserStats getInitial() {
        return new UserStats(0, 0, 0, 0);
    }
}
