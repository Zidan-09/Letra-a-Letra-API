package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyResetTokenUseCase Unit Tests")
class VerifyResetTokenUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private AdminResetTokenRepository tokenRepository;

    @InjectMocks
    private VerifyResetTokenUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully verify token, validate, save repository and return null")
        void execute_WhenTokenIsValid_ShouldValidateSaveAndReturnNull() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "valid-raw-token-123";
            String hashedToken = "hashed-token-123";
            VerifyResetTokenInput input = new VerifyResetTokenInput(email, rawToken);

            Admin admin = mock(Admin.class);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(tokenHashService, adminRepository, tokenRepository, resetToken);
            inOrder.verify(tokenHashService).hash(rawToken);
            inOrder.verify(adminRepository).findByEmail(email);
            inOrder.verify(tokenRepository).findActiveByAdminId(adminId);
            inOrder.verify(resetToken).validate(hashedToken);
            inOrder.verify(tokenRepository).save(resetToken);
        }
    }

    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw InvalidTokenException when admin is not found")
        void execute_WhenAdminNotFound_ShouldThrowInvalidTokenException() {
            String email = "unknown@example.com";
            String rawToken = "some-token";
            String hashedToken = "hashed-some-token";
            VerifyResetTokenInput input = new VerifyResetTokenInput(email, rawToken);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(adminRepository).findByEmail(email);
            verify(tokenRepository, never()).findActiveByAdminId(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token is not found in repository")
        void execute_WhenTokenNotFoundInRepository_ShouldThrowInvalidTokenException() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "unknown-token";
            String hashedToken = "hashed-unknown-token";
            VerifyResetTokenInput input = new VerifyResetTokenInput(email, rawToken);

            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(adminRepository).findByEmail(email);
            verify(tokenRepository).findActiveByAdminId(adminId);
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when reset token domain validation fails and save the attempt")
        void execute_WhenDomainValidationFails_ShouldThrowInvalidTokenExceptionAndSave() {
            String email = "admin@example.com";
            UUID adminId = UUID.randomUUID();
            String rawToken = "expired-or-invalid-token";
            String hashedToken = "hashed-expired-token";
            VerifyResetTokenInput input = new VerifyResetTokenInput(email, rawToken);

            Admin admin = mock(Admin.class);
            AdminPasswordResetToken resetToken = mock(AdminPasswordResetToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenRepository.findActiveByAdminId(adminId)).willReturn(Optional.of(resetToken));
            willThrow(new InvalidTokenException()).given(resetToken).validate(hashedToken);

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(tokenHashService).hash(rawToken);
            verify(adminRepository).findByEmail(email);
            verify(tokenRepository).findActiveByAdminId(adminId);
            verify(resetToken).validate(hashedToken);
            verify(tokenRepository).save(resetToken);
        }
    }
}
