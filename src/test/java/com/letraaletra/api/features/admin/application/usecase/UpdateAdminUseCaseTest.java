package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.UpdateAdminInput;
import com.letraaletra.api.features.admin.application.output.UpdateAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.domain.exception.InvalidAdminOperationException;
import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.permission.Permissions;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateAdminUseCase Unit Tests")
class UpdateAdminUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateAdminUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully update admin when user is super admin promoting target")
        void execute_WhenUserIsSuperAdmin_ShouldUpdateAndPromoteAdmin() {
            UUID principalId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();
            String registeredEmail = "old@letraaletra.com";
            String newEmail = "new@letraaletra.com";
            String newName = "Updated Admin Name";

            AuthenticatedUser principal = new AuthenticatedUser(principalId, "Super Admin", true, true);
            Permission permission = new Permission(PermissionKey.USER, Set.of(PermissionAction.VIEW, PermissionAction.EDIT));
            List<Permission> permissionsList = List.of(permission);

            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetAdminId,
                    newName,
                    newEmail,
                    true,
                    permissionsList
            );

            Admin admin = mock(Admin.class);
            Permissions permissions = mock(Permissions.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(admin));
            given(admin.getEmail()).willReturn(registeredEmail);
            given(adminRepository.existsByEmail(newEmail)).willReturn(false);
            given(admin.getPermissions()).willReturn(permissions);

            UpdateAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(admin, output.admin());

            InOrder inOrder = inOrder(adminChecker, adminRepository, admin, permissions);
            inOrder.verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.EDIT);
            inOrder.verify(adminRepository).find(targetAdminId);
            inOrder.verify(adminRepository).existsByEmail(newEmail);
            inOrder.verify(admin).setName(newName);
            inOrder.verify(admin).setEmail(newEmail);
            inOrder.verify(admin).promoteSuperAdmin();
            inOrder.verify(permissions).set(permission);
            inOrder.verify(adminRepository).save(admin);
        }

        @Test
        @DisplayName("Should successfully update non-super admin when performed by regular admin without changing super status")
        void execute_WhenRegularAdminUpdatesRegularAdmin_ShouldRevokeSuperAndSave() {
            UUID principalId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();
            String email = "regular@letraaletra.com";

            AuthenticatedUser principal = new AuthenticatedUser(principalId, "Admin", true, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetAdminId,
                    "New Name",
                    email,
                    false,
                    List.of()
            );

            Admin admin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(admin));
            given(admin.isSuper()).willReturn(false);
            given(admin.getEmail()).willReturn(email);
            given(adminRepository.existsByEmail(email)).willReturn(false);

            UpdateAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(admin, output.admin());

            verify(admin).revokeSuperAdmin();
            verify(adminRepository).save(admin);
        }

        @Test
        @DisplayName("Should allow updating email to the same registered email without throwing EmailAlreadyInUseException")
        void execute_WhenEmailIsUnchangedAndExistsInRepository_ShouldAllowUpdate() {
            UUID principalId = UUID.randomUUID();
            UUID targetAdminId = UUID.randomUUID();
            String sameEmail = "same@letraaletra.com";

            AuthenticatedUser principal = new AuthenticatedUser(principalId, "Super Admin", true, true);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetAdminId,
                    "Name",
                    sameEmail,
                    true,
                    List.of()
            );

            Admin admin = mock(Admin.class);

            given(adminRepository.find(targetAdminId)).willReturn(Optional.of(admin));
            given(admin.getEmail()).willReturn(sameEmail);
            given(adminRepository.existsByEmail(sameEmail)).willReturn(true);

            UpdateAdminOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(adminRepository).save(admin);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw PermissionDeniedException when adminChecker fails permission check")
        void execute_WhenAdminCheckerFails_ShouldThrowPermissionDeniedException() {
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "User", false, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    UUID.randomUUID(),
                    "Name",
                    "email@letraaletra.com",
                    false,
                    List.of()
            );

            willThrow(new PermissionDeniedException())
                    .given(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.EDIT);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).find(any());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidAdminOperationException when admin attempts to edit their own account")
        void execute_WhenEditingSelf_ShouldThrowInvalidAdminOperationException() {
            UUID sameId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(sameId, "Admin", true, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    sameId,
                    "Name",
                    "email@letraaletra.com",
                    false,
                    List.of()
            );

            assertThrows(InvalidAdminOperationException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.EDIT);
            verify(adminRepository, never()).find(any());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw AdminNotFoundException when target admin does not exist")
        void execute_WhenTargetAdminNotFound_ShouldThrowAdminNotFoundException() {
            UUID targetId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetId,
                    "Name",
                    "email@letraaletra.com",
                    false,
                    List.of()
            );

            given(adminRepository.find(targetId)).willReturn(Optional.empty());

            assertThrows(AdminNotFoundException.class, () -> useCase.execute(input));

            verify(adminRepository).find(targetId);
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw PermissionDeniedException when non-super admin attempts to update a super admin")
        void execute_WhenNonSuperAdminUpdatesSuperAdmin_ShouldThrowPermissionDeniedException() {
            UUID targetId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetId,
                    "Name",
                    "email@letraaletra.com",
                    false,
                    List.of()
            );

            Admin superAdminTarget = mock(Admin.class);

            given(adminRepository.find(targetId)).willReturn(Optional.of(superAdminTarget));
            given(superAdminTarget.isSuper()).willReturn(true);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).existsByEmail(any());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw PermissionDeniedException when non-super admin attempts to alter super status of non-super target")
        void execute_WhenNonSuperAdminTriesToPromoteTarget_ShouldThrowPermissionDeniedException() {
            UUID targetId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetId,
                    "Name",
                    "email@letraaletra.com",
                    true,
                    List.of()
            );

            Admin regularAdminTarget = mock(Admin.class);

            given(adminRepository.find(targetId)).willReturn(Optional.of(regularAdminTarget));
            given(regularAdminTarget.isSuper()).willReturn(false);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).existsByEmail(any());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EmailAlreadyInUseException when new email is already in use by another admin")
        void execute_WhenEmailAlreadyInUse_ShouldThrowEmailAlreadyInUseException() {
            UUID targetId = UUID.randomUUID();
            String registeredEmail = "old@letraaletra.com";
            String occupiedEmail = "occupied@letraaletra.com";

            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Super Admin", true, true);
            UpdateAdminInput input = new UpdateAdminInput(
                    principal,
                    targetId,
                    "Name",
                    occupiedEmail,
                    true,
                    List.of()
            );

            Admin admin = mock(Admin.class);

            given(adminRepository.find(targetId)).willReturn(Optional.of(admin));
            given(admin.getEmail()).willReturn(registeredEmail);
            given(adminRepository.existsByEmail(occupiedEmail)).willReturn(true);

            assertThrows(EmailAlreadyInUseException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).save(any());
        }
    }
}