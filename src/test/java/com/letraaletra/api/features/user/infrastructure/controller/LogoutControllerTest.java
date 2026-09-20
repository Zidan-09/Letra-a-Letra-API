package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.application.usecase.RevokeSessionUseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {
    @Mock
    private RevokeSessionUseCase revokeSessionUseCase;

    @InjectMocks
    private LogoutController controller;

    @Test
    @DisplayName("should revoke the session and return no content")
    void logout() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);

        ResponseEntity<SuccessResponse<Void>> responseEntity = controller.handle(principal);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(revokeSessionUseCase, times(1)).execute(any(RevokeSessionInput.class));
    }
}
