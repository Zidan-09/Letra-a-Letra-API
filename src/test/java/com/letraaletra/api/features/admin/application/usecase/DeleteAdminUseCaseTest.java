package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.DeleteAdminInput;
import com.letraaletra.api.features.admin.application.output.DeleteAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.InvalidAdminOperationException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteAdminUseCase Unit Tests")
class DeleteAdminUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DeleteAdminUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully delete regular admin when principal is super admin")
        void execute_WhenSuperAdminDeletesRegularAdmin_ShouldDeleteAndReturnOutput() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Super Admin", true, true);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);
            Admin targetAdmin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(targetAdmin));

            DeleteAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(targetAdmin, output.admin());

            InOrder inOrder = inOrder(adminChecker, adminRepository);
            inOrder.verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            inOrder.verify(adminRepository).find(targetAdminId);
            inOrder.verify(adminRepository).delete(targetAdmin);
        }

        @Test
        @DisplayName("Should successfully delete super admin when principal is super admin")
        void execute_WhenSuperAdminDeletesSuperAdmin_ShouldDeleteAndReturnOutput() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Super Admin", true, true);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);
            Admin targetAdmin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(targetAdmin));

            DeleteAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(targetAdmin, output.admin());

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository).find(targetAdminId);
            verify(adminRepository).delete(targetAdmin);
        }

        @Test
        @DisplayName("Should successfully delete regular admin when principal is a non-super admin with delete permission")
        void execute_WhenNonSuperAdminDeletesRegularAdmin_ShouldDeleteAndReturnOutput() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Regular Admin", true, false);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);
            Admin targetAdmin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(targetAdmin));
            given(targetAdmin.isSuper()).willReturn(false);

            DeleteAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(targetAdmin, output.admin());

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository).find(targetAdminId);
            verify(adminRepository).delete(targetAdmin);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw PermissionDeniedException when adminChecker fails permission check")
        void execute_WhenAdminCheckerFails_ShouldThrowPermissionDeniedException() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Admin", true, false);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);

            willThrow(new PermissionDeniedException())
                    .given(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).find(any());
            verify(adminRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw InvalidAdminOperationException when admin attempts to delete themselves")
        void execute_WhenAdminTriesToDeleteSelf_ShouldThrowInvalidAdminOperationException() {
            UUID sameAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(sameAdminId, "Self Admin", true, false);
            DeleteAdminInput input = new DeleteAdminInput(principal, sameAdminId);

            assertThrows(InvalidAdminOperationException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository, never()).find(any());
            verify(adminRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw AdminNotFoundException when target admin does not exist in repository")
        void execute_WhenTargetAdminNotFound_ShouldThrowAdminNotFoundException() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Admin", true, false);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.empty());

            assertThrows(AdminNotFoundException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository).find(targetAdminId);
            verify(adminRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw PermissionDeniedException when non-super admin attempts to delete a super admin")
        void execute_WhenNonSuperAdminTriesToDeleteSuperAdmin_ShouldThrowPermissionDeniedException() {
            UUID principalAuthId = UUID.randomUUID();
            UUID superAdminTargetId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Regular Admin", true, false);
            DeleteAdminInput input = new DeleteAdminInput(principal, superAdminTargetId);
            Admin superAdminTarget = mock(Admin.class);

            given(adminRepository.find(superAdminTargetId)).willReturn(Optional.of(superAdminTarget));
            given(superAdminTarget.isSuper()).willReturn(true);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository).find(superAdminTargetId);
            verify(adminRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository.delete fails")
        void execute_WhenRepositoryDeleteFails_ShouldPropagateException() {
            UUID principalAuthId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();

            AuthenticatedUser principal = new AuthenticatedUser(principalAuthId, "Super Admin", true, true);
            DeleteAdminInput input = new DeleteAdminInput(principal, targetAdminId);
            Admin targetAdmin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(targetAdmin));
            doThrow(new RuntimeException("Database error during deletion"))
                    .when(adminRepository).delete(targetAdmin);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.DELETE);
            verify(adminRepository).find(targetAdminId);
            verify(adminRepository).delete(targetAdmin);
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(adminChecker, adminRepository);
        }
    }
}