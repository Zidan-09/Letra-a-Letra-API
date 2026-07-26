package com.letraaletra.api.shared.application.service;

import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.springframework.stereotype.Service;


@Service
public class CheckIfIsAdminService implements AdminChecker {

    public CheckIfIsAdminService() {}

    @Override
    public void check(AuthenticatedUser principal) {
        if (!principal.isAdmin()) throw new UserIsNotAdminException();
    }
}
