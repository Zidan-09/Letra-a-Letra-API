package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetAdminsInput;
import com.letraaletra.api.features.admin.application.output.GetAdminsOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

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
@DisplayName("GetAdminsUseCase Unit Tests")
class GetAdminsUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private GetAdminsUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully return paginated admins when user has permission")
        @SuppressWarnings("unchecked")
        void execute_WhenUserHasPermission_ShouldReturnPaginatedAdmins() {
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            int page = 0;
            int size = 10;
            Sort sort = Sort.by(Sort.Direction.ASC, "name");

            GetAdminsInput input = new GetAdminsInput(principal, page, size, sort);
            Page<Admin> expectedPage = mock(Page.class);

            given(adminRepository.getAdmins(any(AdminsPage.class))).willReturn(expectedPage);

            GetAdminsOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(expectedPage, output.admins());

            InOrder inOrder = inOrder(adminChecker, adminRepository);
            inOrder.verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);

            ArgumentCaptor<AdminsPage> pageCaptor = ArgumentCaptor.forClass(AdminsPage.class);
            inOrder.verify(adminRepository).getAdmins(pageCaptor.capture());

            AdminsPage capturedPage = pageCaptor.getValue();
            assertEquals(page, capturedPage.page());
            assertEquals(size, capturedPage.size());
            assertEquals(sort, capturedPage.sort());
        }

        @Test
        @DisplayName("Should propagate correct pagination parameters including unsorted state to repository")
        @SuppressWarnings("unchecked")
        void execute_WithUnsortedParameters_ShouldPassCorrectAdminsPageToRepository() {
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Super Admin", true, true);
            int page = 2;
            int size = 50;
            Sort sort = Sort.unsorted();

            GetAdminsInput input = new GetAdminsInput(principal, page, size, sort);
            Page<Admin> expectedPage = mock(Page.class);

            given(adminRepository.getAdmins(any(AdminsPage.class))).willReturn(expectedPage);

            useCase.execute(input);

            ArgumentCaptor<AdminsPage> pageCaptor = ArgumentCaptor.forClass(AdminsPage.class);
            verify(adminRepository).getAdmins(pageCaptor.capture());

            AdminsPage capturedPage = pageCaptor.getValue();
            assertEquals(page, capturedPage.page());
            assertEquals(size, capturedPage.size());
            assertEquals(sort, capturedPage.sort());
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw PermissionDeniedException when adminChecker fails permission check")
        void execute_WhenAdminCheckerFails_ShouldThrowPermissionDeniedException() {
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Unauthorized User", false, false);
            GetAdminsInput input = new GetAdminsInput(principal, 0, 10, Sort.unsorted());

            willThrow(new PermissionDeniedException())
                    .given(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);

            assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).getAdmins(any());
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository fails")
        void execute_WhenRepositoryFails_ShouldPropagateException() {
            AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
            GetAdminsInput input = new GetAdminsInput(principal, 0, 10, Sort.unsorted());

            given(adminRepository.getAdmins(any())).willThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminChecker).check(principal, PermissionKey.ADMIN, PermissionAction.VIEW);
            verify(adminRepository).getAdmins(any());
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(adminChecker, adminRepository);
        }
    }
}