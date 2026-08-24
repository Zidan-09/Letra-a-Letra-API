package com.letraaletra.api.shared.domain.security;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public interface RolePrincipalResolver {
    Roles role();

    AuthenticatedUser resolve(TokenContent content);
}
