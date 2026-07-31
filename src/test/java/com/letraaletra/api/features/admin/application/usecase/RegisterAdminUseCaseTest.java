package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.application.port.AdminInvitationEmailService;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.features.admin.domain.TokenHashService;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: RegisterAdminUseCase")
class RegisterAdminUseCaseTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminTokenRepository tokenRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private AdminInvitationEmailService emailService;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RegisterAdminUseCase registerAdminUseCase;

    @Captor
    private ArgumentCaptor<Admin> adminCaptor;

    @Captor
    private ArgumentCaptor<AdminPasswordSetupToken> tokenCaptor;

    private AuthenticatedUser principal;

    private final PermissionKey key = PermissionKey.ADMIN;
    private final PermissionAction action = PermissionAction.CREATE;

    private String validName;
    private String validEmail;

    @BeforeEach
    void setUp() {
        principal = mock(AuthenticatedUser.class);

        validName = "Administrador Master";
        validEmail = "master.admin@letraaletra.com";
    }

    @Test
    @DisplayName("Should successfully register an admin and send invitation email")
    void shouldRegisterAdminSuccessfully() {
        RegisterAdminInput input = new RegisterAdminInput(
                principal,
                validName,
                validEmail
        );

        doNothing()
                .when(adminChecker)
                .check(principal, key, action);

        when(adminRepository.existsByEmail(validEmail))
                .thenReturn(false);

        when(tokenHashService.hash(anyString()))
                .thenReturn("hashed-token");

        doNothing()
                .when(emailService)
                .send(anyString(), anyString(), anyString());


        RegisterAdminOutput output = registerAdminUseCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.admin());

        verify(adminChecker)
                .check(principal, key, action);

        verify(adminRepository)
                .existsByEmail(validEmail);

        verify(adminRepository)
                .save(adminCaptor.capture());

        verify(tokenRepository)
                .save(tokenCaptor.capture());

        verify(emailService)
                .send(
                        eq(validEmail),
                        eq(validName),
                        anyString()
                );

        Admin savedAdmin = adminCaptor.getValue();

        assertEquals(validName, savedAdmin.getName());
        assertEquals(validEmail, savedAdmin.getEmail());
        assertEquals(savedAdmin, output.admin());
    }


    @Test
    @DisplayName("Should throw SecurityException when requester is not authorized")
    void shouldThrowExceptionWhenRequesterIsNotAuthorized() {
        RegisterAdminInput input = new RegisterAdminInput(
                principal,
                validName,
                validEmail
        );

        doThrow(new SecurityException())
                .when(adminChecker)
                .check(principal, key, action);


        assertThrows(
                SecurityException.class,
                () -> registerAdminUseCase.execute(input)
        );


        verify(adminChecker)
                .check(principal, key, action);

        verifyNoInteractions(adminRepository);
        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(emailService);
    }


    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterAdminInput input = new RegisterAdminInput(
                principal,
                validName,
                validEmail
        );


        when(adminRepository.existsByEmail(validEmail))
                .thenReturn(true);


        assertThrows(
                EmailAlreadyInUseException.class,
                () -> registerAdminUseCase.execute(input)
        );


        verify(adminRepository)
                .existsByEmail(validEmail);

        verify(adminRepository, never())
                .save(any());

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(emailService);
    }


    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void shouldPropagateExceptionWhenRepositoryCrashes() {
        RegisterAdminInput input = new RegisterAdminInput(
                principal,
                validName,
                validEmail
        );


        when(adminRepository.existsByEmail(validEmail))
                .thenReturn(false);

        doThrow(new RuntimeException("Database unavailable"))
                .when(adminRepository)
                .save(any(Admin.class));


        assertThrows(
                RuntimeException.class,
                () -> registerAdminUseCase.execute(input)
        );
    }
}