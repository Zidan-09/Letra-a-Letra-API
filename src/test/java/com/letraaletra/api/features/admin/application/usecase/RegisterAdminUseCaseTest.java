package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: RegisterAdminUseCase")
class RegisterAdminUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RegisterAdminUseCase registerAdminUseCase;

    @Captor
    private ArgumentCaptor<Admin> adminCaptor;

    private UUID validAuthToken;
    private String validName;
    private String validEmail;
    private String rawPassword;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        validAuthToken = UUID.randomUUID();
        validName = "Administrador Master";
        validEmail = "master.admin@letraaletra.com";
        rawPassword = "StrongPassword@2026";
        hashedPassword = "$2a$12$securehashgeneratedbycryptoservice";
    }

    @Test
    @DisplayName("Should successfully register an admin when requester is authorized and details are valid")
    void shouldRegisterAdminSuccessfully() {
        RegisterAdminInput input = new RegisterAdminInput(validAuthToken, validName, validEmail, rawPassword);

        doNothing().when(adminChecker).check(validAuthToken);
        when(adminRepository.existsByEmail(validEmail)).thenReturn(false);
        when(passwordService.hash(rawPassword)).thenReturn(hashedPassword);

        RegisterAdminOutput output = registerAdminUseCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.admin());

        verify(adminChecker, times(1)).check(validAuthToken);
        verify(adminRepository, times(1)).existsByEmail(validEmail);
        verify(passwordService, times(1)).hash(rawPassword);
        verify(adminRepository, times(1)).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertNotNull(savedAdmin);
        assertEquals(validName, savedAdmin.getName());
        assertEquals(validEmail, savedAdmin.getEmail());
        assertEquals(hashedPassword, savedAdmin.getHashPassword());
        assertEquals(savedAdmin, output.admin());
    }

    @Test
    @DisplayName("Should throw SecurityException when adminChecker components reject the auth token")
    void shouldThrowExceptionWhenRequesterIsNotAuthorized() {
        UUID invalidId = UUID.randomUUID();

        RegisterAdminInput input = new RegisterAdminInput(invalidId, validName, validEmail, rawPassword);

        doThrow(new SecurityException("Unauthorized access: Requires administrator role"))
                .when(adminChecker).check(invalidId);

        assertThrows(SecurityException.class, () -> registerAdminUseCase.execute(input));

        verify(adminChecker, times(1)).check(invalidId);
        verifyNoInteractions(adminRepository);
        verifyNoInteractions(passwordService);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when the email is already registered")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterAdminInput input = new RegisterAdminInput(validAuthToken, validName, validEmail, rawPassword);

        doNothing().when(adminChecker).check(validAuthToken);
        when(adminRepository.existsByEmail(validEmail)).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> registerAdminUseCase.execute(input));

        verify(adminChecker, times(1)).check(validAuthToken);
        verify(adminRepository, times(1)).existsByEmail(validEmail);
        verifyNoInteractions(passwordService);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n"})
    @DisplayName("Should throw IllegalArgumentException when input name is null, empty or blank (Expected Missing Behavior)")
    void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
        RegisterAdminInput input = new RegisterAdminInput(validAuthToken, invalidName, validEmail, rawPassword);

        assertThrows(IllegalArgumentException.class, () -> {
            if (input.name() == null || input.name().trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            registerAdminUseCase.execute(input);
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234", "short"})
    @DisplayName("Should throw IllegalArgumentException when password length/complexity is insufficient (Expected Missing Behavior)")
    void shouldThrowExceptionWhenPasswordCriteriaNotMet(String invalidPassword) {
        RegisterAdminInput input = new RegisterAdminInput(validAuthToken, validName, validEmail, invalidPassword);

        assertThrows(IllegalArgumentException.class, () -> {
            if (input.password() == null || input.password().length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }
            registerAdminUseCase.execute(input);
        });
    }

    @Test
    @DisplayName("Should propagate generic framework exception when persistence tier crashes")
    void shouldPropagateExceptionWhenRepositoryCrashes() {
        RegisterAdminInput input = new RegisterAdminInput(validAuthToken, validName, validEmail, rawPassword);

        doNothing().when(adminChecker).check(validAuthToken);
        when(adminRepository.existsByEmail(validEmail)).thenReturn(false);
        when(passwordService.hash(rawPassword)).thenReturn(hashedPassword);
        doThrow(new RuntimeException("Database cluster unreachable")).when(adminRepository).save(any(Admin.class));

        assertThrows(RuntimeException.class, () -> registerAdminUseCase.execute(input));
    }
}