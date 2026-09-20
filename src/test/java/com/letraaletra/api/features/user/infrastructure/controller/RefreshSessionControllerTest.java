package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.application.usecase.RefreshSessionUseCase;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.RefreshSessionRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshSessionControllerTest {
    @Mock
    private RefreshSessionUseCase refreshSessionUseCase;

    @InjectMocks
    private RefreshSessionController controller;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should rotate the refresh token and return new credentials")
    void refreshSession() {
        RefreshSessionRequest request = new RefreshSessionRequest("old-refresh-token");

        RefreshSessionOutput output = new RefreshSessionOutput(userId, "new-access-token", "new-refresh-token");

        when(refreshSessionUseCase.execute(any(RefreshSessionInput.class)))
                .thenReturn(output);

        ResponseEntity<SuccessResponse<AuthUserResponse>> responseEntity = controller.handle(request);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        SuccessResponse<AuthUserResponse> body = responseEntity.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.success());
        Assertions.assertNotNull(body.data());
        Assertions.assertEquals(userId.toString(), body.data().id());
        Assertions.assertEquals("new-access-token", body.data().token());
        Assertions.assertEquals("new-refresh-token", body.data().refreshToken());
    }
}
