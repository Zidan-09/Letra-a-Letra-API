package com.letraaletra.api.features.user.domain.factory;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import java.util.List;

public class UserFactory {
    public User createLocal(String id, String nickname, String email, String passwordHash) {
        return new User(
                id,
                nickname,
                email,
                passwordHash,
                null,
                true,
                getInitial(),
                List.of()
        );
    }

    public User createGoogle(String id, String email, String googleId) {
        return new User(
                id,
                null,
                email,
                null,
                googleId,
                true,
                getInitial(),
                List.of()
        );
    }

    private UserStats getInitial() {
        return new UserStats(0, 0, 0, 0);
    }
}