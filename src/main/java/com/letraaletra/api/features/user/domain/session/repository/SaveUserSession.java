package com.letraaletra.api.features.user.domain.session.repository;

import com.letraaletra.api.features.user.domain.session.UserSession;

public interface SaveUserSession {
    void save(UserSession session);
}
