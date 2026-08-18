package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ForgotAdminPasswordInput;
import com.letraaletra.api.features.admin.application.port.PasswordResetTokenEmailService;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotAdminPasswordUseCase Unit Tests")
class ForgotAdminPasswordUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private AdminResetTokenRepository tokenRepository;

    @Mock
    private PasswordResetTokenEmailService emailService;

    @InjectMocks
    private ForgotAdminPasswordUseCase useCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should process password reset request when admin is found")
        void execute_WhenAdminExists_ShouldInvalidateTokensSaveNewTokenAndSendEmail() {
            String email = "admin@letraaletra.com";
            String adminName = "Admin User";
            UUID adminId = UUID.randomUUID();
            UUID generatedTokenUuid = UUID.randomUUID();
            String rawToken = generatedTokenUuid.toString();
            String hashedToken = "hashed-reset-token";

            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(email);
            Admin admin = mock(Admin.class);
            AdminPasswordResetToken createdToken = mock(AdminPasswordResetToken.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(admin.getEmail()).willReturn(email);
            given(admin.getName()).willReturn(adminName);
            given(tokenHashService.hash(rawToken)).willReturn(hashedToken);

            try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class);
                 MockedStatic<AdminPasswordResetToken> mockedTokenClass = mockStatic(AdminPasswordResetToken.class)) {

                mockedUuid.when(UUID::randomUUID).thenReturn(generatedTokenUuid);
                mockedTokenClass.when(() -> AdminPasswordResetToken.create(adminId, hashedToken)).thenReturn(createdToken);

                Void result = useCase.execute(input);

                assertNull(result);

                InOrder inOrder = inOrder(adminRepository, tokenRepository, tokenHashService, emailService);
                inOrder.verify(adminRepository).findByEmail(email);
                inOrder.verify(tokenRepository).invalidateAllByAdminId(adminId);
                inOrder.verify(tokenHashService).hash(rawToken);
                inOrder.verify(tokenRepository).save(createdToken);
                inOrder.verify(emailService).send(email, adminName, rawToken);
            }
        }

        @Test
        @DisplayName("Should pass correctly hashed token payload to factory and repository")
        void execute_WhenAdminExists_ShouldPassHashedTokenToRepository() {
            String email = "admin@letraaletra.com";
            UUID adminId = UUID.randomUUID();
            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(email);
            Admin admin = mock(Admin.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(admin.getEmail()).willReturn(email);
            given(admin.getName()).willReturn("Admin");
            given(tokenHashService.hash(any())).willReturn("hashed-value");

            useCase.execute(input);

            ArgumentCaptor<AdminPasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(AdminPasswordResetToken.class);
            verify(tokenRepository).save(tokenCaptor.capture());

            AdminPasswordResetToken savedToken = tokenCaptor.getValue();
            assertNotNull(savedToken);
        }

        @Test
        @DisplayName("Should silently complete and not send email when admin is not found (anti-enumeration defense)")
        void execute_WhenAdminNotFound_ShouldReturnNullWithoutSideEffects() {
            String nonexistentEmail = "unknown@letraaletra.com";
            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(nonexistentEmail);

            given(adminRepository.findByEmail(nonexistentEmail)).willReturn(Optional.empty());

            Void result = useCase.execute(input);

            assertNull(result);

            verify(adminRepository).findByEmail(nonexistentEmail);
            verify(tokenRepository, never()).invalidateAllByAdminId(any());
            verify(tokenHashService, never()).hash(any());
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).send(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Exception and Edge Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should propagate exception when tokenRepository.invalidateAllByAdminId fails")
        void execute_WhenInvalidateTokensFails_ShouldPropagateException() {
            String email = "admin@letraaletra.com";
            UUID adminId = UUID.randomUUID();
            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(email);
            Admin admin = mock(Admin.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            doThrow(new RuntimeException("Database error during token invalidation"))
                    .when(tokenRepository).invalidateAllByAdminId(adminId);

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(tokenHashService, never()).hash(any());
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).send(any(), any(), any());
        }

        @Test
        @DisplayName("Should propagate exception when tokenRepository.save fails")
        void execute_WhenTokenRepositorySaveFails_ShouldPropagateException() {
            String email = "admin@letraaletra.com";
            UUID adminId = UUID.randomUUID();
            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(email);
            Admin admin = mock(Admin.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(tokenHashService.hash(any())).willReturn("hashed-token");
            doThrow(new RuntimeException("Database error during token save"))
                    .when(tokenRepository).save(any());

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(emailService, never()).send(any(), any(), any());
        }

        @Test
        @DisplayName("Should propagate exception when emailService.send fails")
        void execute_WhenEmailServiceFails_ShouldPropagateException() {
            String email = "admin@letraaletra.com";
            String adminName = "Admin User";
            UUID adminId = UUID.randomUUID();
            ForgotAdminPasswordInput input = new ForgotAdminPasswordInput(email);
            Admin admin = mock(Admin.class);

            given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
            given(admin.getId()).willReturn(adminId);
            given(admin.getEmail()).willReturn(email);
            given(admin.getName()).willReturn(adminName);
            given(tokenHashService.hash(any())).willReturn("hashed-token");
            doThrow(new RuntimeException("SMTP delivery failed"))
                    .when(emailService).send(eq(email), eq(adminName), any());

            assertThrows(RuntimeException.class, () -> useCase.execute(input));

            verify(tokenRepository).save(any());
        }

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> useCase.execute(null));

            verifyNoInteractions(adminRepository, tokenHashService, tokenRepository, emailService);
        }
    }
}