package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ActivateAccountInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivateAccountUseCase Unit Tests")
class ActivateAccountUseCaseTest {

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminTokenRepository tokenRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private ActivateAccountUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should successfully activate account, mark token as used, and save entities in correct order")
        void execute_WithValidTokenAndAdmin_ShouldActivateAccountAndSaveEntities() {
            String rawToken = "valid-token-123";
            String hashedToken = "hashed-token-123";
            String rawPassword = "securePassword123!";
            String hashedPassword = "hashedPassword123!";
            UUID adminId = UUID.randomUUID();

            ActivateAccountInput input = new ActivateAccountInput(rawToken, rawPassword);

            AdminPasswordSetupToken setupToken = mock(AdminPasswordSetupToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.of(setupToken));
            given(setupToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
            given(passwordService.hash(rawPassword)).willReturn(hashedPassword);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(tokenHashService, tokenRepository, setupToken, adminRepository, passwordService, admin);
            inOrder.verify(tokenHashService).hash(rawToken);
            inOrder.verify(tokenRepository).findByTokenHash(hashedToken);
            inOrder.verify(setupToken).validate();
            inOrder.verify(setupToken).getAdminId();
            inOrder.verify(adminRepository).find(adminId);
            inOrder.verify(passwordService).hash(rawPassword);
            inOrder.verify(admin).activateAccount(hashedPassword);
            inOrder.verify(setupToken).markAsUsed();
            inOrder.verify(adminRepository).save(admin);
            inOrder.verify(tokenRepository).save(setupToken);
        }
    }

    @Nested
    @DisplayName("Validation and Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw InvalidTokenException when token is not found in repository")
        void execute_WhenTokenNotFound_ShouldThrowInvalidTokenException() {
            String rawToken = "invalid-token";
            String hashedToken = "hashed-invalid-token";
            ActivateAccountInput input = new ActivateAccountInput(rawToken, "password123");

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(adminRepository, never()).find(any());
            verify(passwordService, never()).hash(any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException and halt execution when token validation fails (expired or used)")
        void execute_WhenTokenValidationFails_ShouldThrowInvalidTokenException() {
            String rawToken = "invalid-state-token";
            String hashedToken = "hashed-token";
            ActivateAccountInput input = new ActivateAccountInput(rawToken, "password123");

            AdminPasswordSetupToken setupToken = mock(AdminPasswordSetupToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.of(setupToken));
            doThrow(new InvalidTokenException()).when(setupToken).validate();

            assertThrows(InvalidTokenException.class, () -> useCase.execute(input));

            verify(setupToken).validate();
            verify(setupToken, never()).getAdminId();
            verify(adminRepository, never()).find(any());
            verify(passwordService, never()).hash(any());
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw AdminNotFoundException when admin associated with token is not found")
        void execute_WhenAdminNotFound_ShouldThrowAdminNotFoundException() {
            String rawToken = "valid-token";
            String hashedToken = "hashed-valid-token";
            UUID adminId = UUID.randomUUID();
            ActivateAccountInput input = new ActivateAccountInput(rawToken, "password123");

            AdminPasswordSetupToken setupToken = mock(AdminPasswordSetupToken.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.of(setupToken));
            given(setupToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.empty());

            assertThrows(AdminNotFoundException.class, () -> useCase.execute(input));

            verify(setupToken).validate();
            verify(passwordService, never()).hash(any());
            verify(setupToken, never()).markAsUsed();
            verify(adminRepository, never()).save(any());
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should propagate exception when adminRepository.save fails")
        void execute_WhenAdminRepositorySaveFails_ShouldPropagateException() {
            String rawToken = "valid-token";
            String hashedToken = "hashed-token";
            String rawPassword = "password123";
            String hashedPassword = "hashedPassword";
            UUID adminId = UUID.randomUUID();

            ActivateAccountInput input = new ActivateAccountInput(rawToken, rawPassword);

            AdminPasswordSetupToken setupToken = mock(AdminPasswordSetupToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.of(setupToken));
            given(setupToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
            given(passwordService.hash(rawPassword)).willReturn(hashedPassword);

            doThrow(new RuntimeException("Database error during admin save")).when(adminRepository).save(admin);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminRepository).save(admin);
            verify(tokenRepository, never()).save(setupToken);
        }

        @Test
        @DisplayName("Should propagate exception when tokenRepository.save fails")
        void execute_WhenTokenRepositorySaveFails_ShouldPropagateException() {
            String rawToken = "valid-token";
            String hashedToken = "hashed-token";
            String rawPassword = "password123";
            String hashedPassword = "hashedPassword";
            UUID adminId = UUID.randomUUID();

            ActivateAccountInput input = new ActivateAccountInput(rawToken, rawPassword);

            AdminPasswordSetupToken setupToken = mock(AdminPasswordSetupToken.class);
            Admin admin = mock(Admin.class);

            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);
            given(tokenRepository.findByTokenHash(hashedToken)).willReturn(Optional.of(setupToken));
            given(setupToken.getAdminId()).willReturn(adminId);
            given(adminRepository.find(adminId)).willReturn(Optional.of(admin));
            given(passwordService.hash(rawPassword)).willReturn(hashedPassword);

            doThrow(new RuntimeException("Database error during token save")).when(tokenRepository).save(setupToken);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(adminRepository).save(admin);
            verify(tokenRepository).save(setupToken);
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(tokenHashService, tokenRepository, adminRepository, passwordService);
        }
    }
}