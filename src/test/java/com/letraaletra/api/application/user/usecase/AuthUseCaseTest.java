package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.application.usecase.user.AuthUseCase;
import com.letraaletra.api.infrastructure.security.BCryptPasswordService;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.request.user.SignInRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.SignInResponseDTO;
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
    private JsonWebTokenService jsonWebTokenService;

    @Mock
    private BCryptPasswordService BCryptPasswordService;

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

        Mockito.when(BCryptPasswordService.matches("12345", "hash"))
                .thenReturn(true);

        Mockito.when(jsonWebTokenService.generateToken(user.getId()))
                .thenReturn("fake-token");

        SignInResponseDTO result = authUseCase.login(
                new SignInRequestDTO("email@email.com", "12345")
        );

        Assertions.assertEquals("fake-token", result.token());
        Mockito.verify(jsonWebTokenService).generateToken("id");
    }

    @Test
    @DisplayName("Should throw a UserNotFound")
    void notAuthBecauseUser() {
        Mockito.when(userRepository.findByEmail("email@email.com"))
                .thenReturn(null);

        SignInRequestDTO request = new SignInRequestDTO("email@email.com", "12345");

        Assertions.assertThrows(UserNotFoundException.class, () -> authUseCase.login(request));

        Mockito.verify(jsonWebTokenService, Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    @DisplayName("Should throw an InvalidPasswordException")
    void notAuthBecausePassword() {
        User user = new User("id", "Samuel", "avatar1", "email@email.com", "hash");

        Mockito.when(userRepository.findByEmail("email@email.com"))
                .thenReturn(user);

        Mockito.when(BCryptPasswordService.matches("12345", "hash"))
                .thenReturn(false);

        SignInRequestDTO request = new SignInRequestDTO("email@email.com", "12345");

        Assertions.assertThrows(InvalidPasswordException.class, () -> authUseCase.login(request));

        Mockito.verify(jsonWebTokenService, Mockito.never()).generateToken(Mockito.any());
    }
}