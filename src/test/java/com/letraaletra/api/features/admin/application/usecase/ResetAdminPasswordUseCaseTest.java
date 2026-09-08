package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.features.user.domain.reset.exception.SamePasswordException;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetAdminPasswordUseCase Unit Tests")
class ResetAdminPasswordUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private AdminResetTokenRepository tokenRepository;

    @InjectMocks
    private ResetAdminPasswordUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully reset admin password when token and input are valid")
        void execute_WhenTokenAndPasswordAreValid_ShouldResetPasswordAndSaveEntities() {
            String email = "admin@example.com";
            String rawToken = "valid-raw-token";
            String tokenHash = "hashed-token";
            String newPassword = "NewSecurePassword123!";
            String newPasswordHash = "new-hashed-password";
            String currentPasswordHash = "current-hashed-password";
            UUID adminId = UUID.randomUUID();

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, newPassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));
            given(admin.getPasswordHash()).willReturn(currentPasswordHash);
            given(passwordService.matches(newPassword, currentPasswordHash)).willReturn(false);
            given(passwordService.hash(newPassword)).willReturn(newPasswordHash);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(
                    tokenHashService,
                    adminRepository,
                    tokenRepository,
                    resetToken,
                    passwordService,
                    admin
            );

            inOrder.verify(tokenHashService).hash(rawToken);
            inOrder.verify(adminRepository).findByEmail(email);
            inOrder.verify(tokenRepository).findActiveByAdminId(adminId);
            inOrder.verify(resetToken).validate(tokenHash);
            inOrder.verify(passwordService).matches(newPassword, currentPasswordHash);
            inOrder.verify(resetToken).markAsUsed();
            inOrder.verify(passwordService).hash(newPassword);
            inOrder.verify(admin).changePassword(newPasswordHash);
            inOrder.verify(adminRepository).save(admin);
            inOrder.verify(tokenRepository).save(resetToken);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw InvalidTokenException when admin is not found")
        void execute_WhenAdminNotFound_ShouldThrowInvalidTokenException() {
            String email = "unknown@example.com";
            String rawToken = "some-token";
            String tokenHash = "hashed-some-token";
            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, "NewPassword123!", rawToken);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(adminRepository).findByEmail(email);
            verify(tokenRepository, never()).findActiveByAdminId(any());
            verify(passwordService, never()).matches(any(), any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token is not found in repository")
        void execute_WhenTokenNotFound_ShouldThrowInvalidTokenException() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "non-existent-token";
            String tokenHash = "hashed-non-existent-token";
            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, "NewPassword123!", rawToken);

            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(adminRepository).findByEmail(email);
            verify(tokenRepository).findActiveByAdminId(adminId);
            verify(adminRepository, never()).save(any());
            verify(passwordService, never()).matches(any(), any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token validation fails (expired or used) and save the attempt")
        void execute_WhenTokenValidationFails_ShouldThrowInvalidTokenExceptionAndSave() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "expired-token";
            String tokenHash = "hashed-expired-token";
            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, "NewPassword123!", rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));
            willThrow(new InvalidTokenException()).given(resetToken).validate(tokenHash);

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(resetToken).validate(tokenHash);
            verify(tokenRepository).save(resetToken);
            verify(passwordService, never()).matches(any(), any());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw SamePasswordException when new password matches the current password")
        void execute_WhenNewPasswordIsSameAsCurrent_ShouldThrowSamePasswordException() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "valid-token";
            String tokenHash = "hashed-token";
            String samePassword = "SamePassword123!";
            String currentPasswordHash = "current-hashed-password";

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, samePassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));
            given(admin.getPasswordHash()).willReturn(currentPasswordHash);
            given(passwordService.matches(samePassword, currentPasswordHash)).willReturn(true);

            assertThrows(SamePasswordException.class, () -> useCase.execute(input));

            verify(resetToken, never()).markAsUsed();
            verify(passwordService, never()).hash(any());
            verify(admin, never()).changePassword(any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository.save fails")
        void execute_WhenRepositorySaveFails_ShouldPropagateException() {
            String email = "admin@example.com";
            String rawToken = "valid-token";
            String tokenHash = "hashed-token";
            String newPassword = "NewPassword123!";
            UUID adminId = UUID.randomUUID();

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(email, newPassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));
            given(admin.getPasswordHash()).willReturn("old-hash");
            given(passwordService.matches(newPassword, "old-hash")).willReturn(false);
            given(passwordService.hash(newPassword)).willReturn("new-hash");

            doThrow(new RuntimeException("Database save error")).when(adminRepository).save(admin);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminRepository).save(admin);
            verify(tokenRepository, never()).save(any());
        }
    }
}
