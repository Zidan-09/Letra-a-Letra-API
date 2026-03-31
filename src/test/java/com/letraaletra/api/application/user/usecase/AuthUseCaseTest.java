package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.application.user.service.PasswordService;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.request.user.LoginRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.LoginResponseDTO;
import com.letraaletra.api.domain.user.exceptions.InvalidPasswordException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private GlobalTokenService globalTokenService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    @DisplayName("Should return a jwt with the user id")
    void login() {
        User user = new User("id", "Samuel", "avatar1", "email@email.com", "hash");

        Mockito.when(userRepository.findByEmail("email@email.com"))
                .thenReturn(user);

        Mockito.when(passwordService.matches("12345", "hash"))
                .thenReturn(true);

        Mockito.when(globalTokenService.generateToken(user.getId()))
                .thenReturn("fake-token");

        LoginResponseDTO result = authUseCase.login(
                new LoginRequestDTO("email@email.com", "12345")
        );

        Assertions.assertEquals("fake-token", result.token());
        Mockito.verify(globalTokenService).generateToken("id");
    }

    @Test
    @DisplayName("Should throw a UserNotFound")
    void notAuthBecauseUser() {
        Mockito.when(userRepository.findByEmail("email@email.com"))
                .thenReturn(null);

        LoginRequestDTO request = new LoginRequestDTO("email@email.com", "12345");

        Assertions.assertThrows(UserNotFoundException.class, () -> authUseCase.login(request));

        Mockito.verify(globalTokenService, Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    @DisplayName("Should throw an InvalidPasswordException")
    void notAuthBecausePassword() {
        User user = new User("id", "Samuel", "avatar1", "email@email.com", "hash");

        Mockito.when(userRepository.findByEmail("email@email.com"))
                .thenReturn(user);

        Mockito.when(passwordService.matches("12345", "hash"))
                .thenReturn(false);

        LoginRequestDTO request = new LoginRequestDTO("email@email.com", "12345");

        Assertions.assertThrows(InvalidPasswordException.class, () -> authUseCase.login(request));

        Mockito.verify(globalTokenService, Mockito.never()).generateToken(Mockito.any());
    }
}