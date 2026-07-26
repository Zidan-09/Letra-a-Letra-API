package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public interface AdminChecker {
    void check(AuthenticatedUser principal);
}
