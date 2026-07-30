package com.letraaletra.api.shared.application.service;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.permission.Permissions;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CheckIfIsAdminServiceTest {

    private AdminRepository adminRepository;
    private CheckIfIsAdminService service;

    @BeforeEach
    void setUp() {
        adminRepository = mock(AdminRepository.class);
        service = new CheckIfIsAdminService(adminRepository);
    }

    @Test
    @DisplayName("Should throw an UserIsNotAdminException when User is not Admin")
    void shouldThrowWhenUserIsNotAdmin() {
        AuthenticatedUser principal = new AuthenticatedUser(
                UUID.randomUUID(),
                "Samuel",
                false
        );

        assertThrows(
                UserIsNotAdminException.class,
                () -> service.check(principal, PermissionKey.ADMIN, PermissionAction.VIEW)
        );

        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("Should throw an AdminNotFoundException when the repository dont returns an Admin with the UUID")
    void shouldThrowWhenAdminIsNotFound() {
        UUID authId = UUID.randomUUID();

        AuthenticatedUser principal = new AuthenticatedUser(
                authId,
                "Samuel",
                true
        );

        when(adminRepository.find(authId))
                .thenReturn(Optional.empty());

        assertThrows(
                AdminNotFoundException.class,
                () -> service.check(principal, PermissionKey.ADMIN, PermissionAction.VIEW)
        );

        verify(adminRepository).find(authId);
    }

    @Test
    @DisplayName("Should throw a PermissionDeniedException when Admin dont have permission")
    void shouldThrowWhenPermissionIsDenied() {
        UUID authId = UUID.randomUUID();

        AuthenticatedUser principal = new AuthenticatedUser(
                authId,
                "Samuel",
                true
        );

        Admin admin = mock(Admin.class);
        Permissions permissions = mock(Permissions.class);

        when(adminRepository.find(authId))
                .thenReturn(Optional.of(admin));

        when(admin.getPermissions())
                .thenReturn(permissions);

        when(permissions.can(PermissionKey.ADMIN, PermissionAction.VIEW))
                .thenReturn(false);

        assertThrows(
                PermissionDeniedException.class,
                () -> service.check(principal, PermissionKey.ADMIN, PermissionAction.VIEW)
        );
    }

    @Test
    @DisplayName("Should pass the verification correctly")
    void shouldAllowWhenPermissionExists() {
        UUID authId = UUID.randomUUID();

        AuthenticatedUser principal = new AuthenticatedUser(
                authId,
                "Samuel",
                true
        );

        Admin admin = mock(Admin.class);
        Permissions permissions = mock(Permissions.class);

        when(adminRepository.find(authId))
                .thenReturn(Optional.of(admin));

        when(admin.getPermissions())
                .thenReturn(permissions);

        when(permissions.can(PermissionKey.ADMIN, PermissionAction.VIEW))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> service.check(principal, PermissionKey.ADMIN, PermissionAction.VIEW)
        );

        verify(adminRepository).find(authId);
        verify(admin).getPermissions();
        verify(permissions).can(PermissionKey.ADMIN, PermissionAction.VIEW);
    }
}