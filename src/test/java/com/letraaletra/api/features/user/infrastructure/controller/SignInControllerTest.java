package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.usecase.SignInUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.SignInRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;
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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class SignInControllerTest {
    @Mock
    private SignInUseCase signInUseCase;

    @InjectMocks
    private SignInController controller;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should get the request to sign in an user and return an response correctly")
    void signInUser() {
        SignInRequest request = new SignInRequest("teste@email.com", "12341234");

        SignInOutput output = new SignInOutput(userId, "token");

        when(signInUseCase.execute(any(SignInInput.class)))
                .thenReturn(output);

        ResponseEntity<SuccessResponse<SignInResponse>> responseEntity = controller.signIn(request);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        SuccessResponse<SignInResponse> body = responseEntity.getBody();
        Assertions.assertNotNull(body);

        Assertions.assertEquals(UserMessages.USER_LOGGED.getMessage(), body.message());
        Assertions.assertNotNull(body.data());
        Assertions.assertTrue(body.success());
    }
}