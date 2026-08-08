package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetMyAdminProfileInput;
import com.letraaletra.api.features.admin.application.output.GetMyAdminProfileOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyAdminProfileUseCase Unit Tests")
class GetMyAdminProfileUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private GetMyAdminProfileUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully return admin profile when admin exists")
        void execute_WhenAdminExists_ShouldReturnOutput() {
            UUID adminId = UUID.randomUUID();
            GetMyAdminProfileInput input = new GetMyAdminProfileInput(adminId);
            Admin expectedAdmin = mock(Admin.class);

            given(adminRepository.find(adminId)).willReturn(Optional.of(expectedAdmin));

            GetMyAdminProfileOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(expectedAdmin, output.admin());

            verify(adminRepository).find(adminId);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw AdminNotFoundException when admin is not found in repository")
        void execute_WhenAdminNotFound_ShouldThrowAdminNotFoundException() {
            UUID adminId = UUID.randomUUID();
            GetMyAdminProfileInput input = new GetMyAdminProfileInput(adminId);

            given(adminRepository.find(adminId)).willReturn(Optional.empty());

            assertThrows(AdminNotFoundException.class, () -> useCase.execute(input));

            verify(adminRepository).find(adminId);
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository fails")
        void execute_WhenRepositoryFails_ShouldPropagateException() {
            UUID adminId = UUID.randomUUID();
            GetMyAdminProfileInput input = new GetMyAdminProfileInput(adminId);

            given(adminRepository.find(adminId)).willThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminRepository).find(adminId);
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(adminRepository);
        }
    }
}