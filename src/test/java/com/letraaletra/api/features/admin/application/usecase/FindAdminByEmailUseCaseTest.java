package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.FindAdminByEmailInput;
import com.letraaletra.api.features.admin.application.output.FindAdminByEmailOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindAdminByEmailUseCase Unit Tests")
class FindAdminByEmailUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private FindAdminByEmailUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully find admin by email when permission check passes and admin exists")
        void execute_WhenAdminExistsAndHasPermission_ShouldReturnOutput() {
            String email = "admin@letraaletra.com";
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            FindAdminByEmailInput input = new FindAdminByEmailInput(principal, email);
            Admin expectedAdmin = mock(Admin.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(expectedAdmin));

            FindAdminByEmailOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(expectedAdmin, output.admin());

            InOrder inOrder = inOrder(adminChecker, adminRepository);
            inOrder.verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);
            inOrder.verify(adminRepository).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw PermissionDeniedException when adminChecker fails permission check")
        void execute_WhenAdminCheckerFails_ShouldThrowPermissionDeniedException() {
            String email = "admin@letraaletra.com";
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "User", false, false);
            FindAdminByEmailInput input = new FindAdminByEmailInput(principal, email);

            willThrow(new PermissionDeniedException())
                    .given(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).findByEmail(any());
        }

        @Test
        @DisplayName("Should throw AdminNotFoundException when admin is not found in repository")
        void execute_WhenAdminNotFound_ShouldThrowAdminNotFoundException() {
            String email = "nonexistent@letraaletra.com";
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            FindAdminByEmailInput input = new FindAdminByEmailInput(principal, email);

            given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

            assertThrows(AdminNotFoundException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);
            verify(adminRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository fails")
        void execute_WhenRepositoryFails_ShouldPropagateException() {
            String email = "admin@letraaletra.com";
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            FindAdminByEmailInput input = new FindAdminByEmailInput(principal, email);

            given(adminRepository.findByEmail(email)).willThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);
            verify(adminRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(adminChecker, adminRepository);
        }
    }
}