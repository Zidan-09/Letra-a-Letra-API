package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.features.user.domain.exception.SamePasswordException;
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
            String rawToken = "valid-raw-token";
            String tokenHash = "hashed-token";
            String newPassword = "NewSecurePassword123!";
            String newPasswordHash = "new-hashed-password";
            String currentPasswordHash = "current-hashed-password";
            UUID adminId = UUID.randomUUID();

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(newPassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.of(resetToken));
            given(resetToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
            given(admin.getPasswordHash()).willReturn(currentPasswordHash);
            given(passwordService.matches(newPassword, currentPasswordHash)).willReturn(false);
            given(passwordService.hash(newPassword)).willReturn(newPasswordHash);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(
                    tokenHashService,
                    tokenRepository,
                    resetToken,
                    adminRepository,
                    passwordService,
                    admin
            );

            inOrder.verify(tokenHashService).hash(rawToken);
            inOrder.verify(tokenRepository).findByTokenHash(tokenHash);
            inOrder.verify(resetToken).validate(tokenHash);
            inOrder.verify(adminRepository).find(adminId);
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
        @DisplayName("Should throw InvalidTokenException when token is not found in repository")
        void execute_WhenTokenNotFound_ShouldThrowInvalidTokenException() {
            String rawToken = "non-existent-token";
            String tokenHash = "hashed-non-existent-token";
            ResetAdminPasswordInput input = new ResetAdminPasswordInput("NewPassword123!", rawToken);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(tokenRepository).findByTokenHash(tokenHash);
            verify(adminRepository, never()).find(any());
            verify(passwordService, never()).matches(any(), any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token validation fails (expired or used)")
        void execute_WhenTokenValidationFails_ShouldThrowInvalidTokenException() {
            String rawToken = "expired-token";
            String tokenHash = "hashed-expired-token";
            ResetAdminPasswordInput input = new ResetAdminPasswordInput("NewPassword123!", rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.of(resetToken));
            willThrow(new InvalidTokenException()).given(resetToken).validate(tokenHash);

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(resetToken).validate(tokenHash);
            verify(adminRepository, never()).find(any());
            verify(passwordService, never()).matches(any(), any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when admin associated with token does not exist")
        void execute_WhenAdminNotFound_ShouldThrowInvalidTokenException() {
            String rawToken = "valid-token";
            String tokenHash = "hashed-token";
            UUID adminId = UUID.randomUUID();
            ResetAdminPasswordInput input = new ResetAdminPasswordInput("NewPassword123!", rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.of(resetToken));
            given(resetToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(resetToken).validate(tokenHash);
            verify(adminRepository).find(adminId);
            verify(passwordService, never()).matches(any(), any());
            verify(resetToken, never()).markAsUsed();
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw SamePasswordException when new password matches the current password")
        void execute_WhenNewPasswordIsSameAsCurrent_ShouldThrowSamePasswordException() {
            String rawToken = "valid-token";
            String tokenHash = "hashed-token";
            String samePassword = "SamePassword123!";
            String currentPasswordHash = "current-hashed-password";
            UUID adminId = UUID.randomUUID();

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(samePassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.of(resetToken));
            given(resetToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
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
            String rawToken = "valid-token";
            String tokenHash = "hashed-token";
            String newPassword = "NewPassword123!";
            UUID adminId = UUID.randomUUID();

            ResetAdminPasswordInput input = new ResetAdminPasswordInput(newPassword, rawToken);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(tokenHash);
            given(tokenRepository.findByTokenHash(tokenHash)).willReturn(Optional.of(resetToken));
            given(resetToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
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