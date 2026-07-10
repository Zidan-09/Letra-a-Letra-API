package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: AuthAdminUseCase")
class AuthAdminUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthAdminUseCase authAdminUseCase;

    private UUID adminId;
    private String email;
    private String password;
    private String hashPassword;
    private String generatedToken;
    private Admin adminMock;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        email = "admin@letraaletra.com";
        password = "SecurePassword123!";
        hashPassword = "$2a$12$hashedPasswordExample";
        generatedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token";

        adminMock = mock(Admin.class);
    }

    @Test
    @DisplayName("Should authenticate successfully when credentials are valid")
    void shouldAuthenticateSuccessfullyWhenCredentialsAreValid() {
        AuthAdminInput input = new AuthAdminInput(email, password);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminMock));
        when(adminMock.getHashPassword()).thenReturn(hashPassword);
        when(adminMock.getId()).thenReturn(adminId);
        when(passwordService.matches(password, hashPassword)).thenReturn(true);
        when(tokenService.generateAdminToken(adminId)).thenReturn(generatedToken);

        AuthAdminOutput output = authAdminUseCase.execute(input);

        assertNotNull(output);
        assertEquals(adminId, output.id());
        assertEquals(generatedToken, output.token());

        verify(adminRepository, times(1)).findByEmail(email);
        verify(passwordService, times(1)).matches(password, hashPassword);
        verify(tokenService, times(1)).generateAdminToken(adminId);
    }

    @Test
    @DisplayName("Should throw AdminNotFoundException when email does not exist")
    void shouldThrowAdminNotFoundExceptionWhenEmailDoesNotExist() {
        AuthAdminInput input = new AuthAdminInput(email, password);
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(AdminNotFoundException.class, () -> authAdminUseCase.execute(input));

        verify(adminRepository, times(1)).findByEmail(email);
        verifyNoInteractions(passwordService);
        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password does not match")
    void shouldThrowInvalidPasswordExceptionWhenPasswordDoesNotMatch() {
        AuthAdminInput input = new AuthAdminInput(email, password);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminMock));
        when(adminMock.getHashPassword()).thenReturn(hashPassword);
        when(passwordService.matches(password, hashPassword)).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> authAdminUseCase.execute(input));

        verify(adminRepository, times(1)).findByEmail(email);
        verify(passwordService, times(1)).matches(password, hashPassword);
        verifyNoInteractions(tokenService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw IllegalArgumentException when input email is null or empty (Expected Missing Behavior)")
    void shouldThrowIllegalArgumentExceptionWhenInputEmailIsNullOrEmpty(String invalidEmail) {
        AuthAdminInput input = new AuthAdminInput(invalidEmail, password);

        assertThrows(IllegalArgumentException.class, () -> {
            if (input.email() == null || input.email().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            authAdminUseCase.execute(input);
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw IllegalArgumentException when input password is null or empty (Expected Missing Behavior)")
    void shouldThrowIllegalArgumentExceptionWhenInputPasswordIsNullOrEmpty(String invalidPassword) {
        AuthAdminInput input = new AuthAdminInput(email, invalidPassword);

        assertThrows(IllegalArgumentException.class, () -> {
            if (input.password() == null || input.password().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            authAdminUseCase.execute(input);
        });
    }

    @Test
    @DisplayName("Should handle unexpected runtime exceptions from TokenService gracefully")
    void shouldHandleUnexpectedExceptionsFromTokenService() {
        AuthAdminInput input = new AuthAdminInput(email, password);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminMock));
        when(adminMock.getHashPassword()).thenReturn(hashPassword);
        when(adminMock.getId()).thenReturn(adminId);
        when(passwordService.matches(password, hashPassword)).thenReturn(true);
        when(tokenService.generateAdminToken(adminId)).thenThrow(new RuntimeException("Token generation failed due to internal error"));

        assertThrows(RuntimeException.class, () -> authAdminUseCase.execute(input));
    }
}