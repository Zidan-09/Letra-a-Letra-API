package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.ChangeNicknameUseCase;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeNicknameResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChangeNicknameControllerTest {
    @Mock
    private ChangeNicknameUseCase changeNicknameUseCase;

    @InjectMocks
    private ChangeNicknameController controller;

    @Test
    @DisplayName("should get the request to update the nickname and return an response correctly")
    void updateNickname() {
        ChangeNicknameRequest request = new ChangeNicknameRequest("nickname-test-123");

        ChangeNicknameOutput output = new ChangeNicknameOutput(mock(User.class));

        Mockito.when(changeNicknameUseCase.execute(Mockito.any(ChangeNicknameInput.class)))
                .thenReturn(output);

        ResponseEntity<SuccessResponse<ChangeNicknameResponse>> responseEntity = controller.updateNickname(UUID.randomUUID(), request);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        SuccessResponse<ChangeNicknameResponse> body = responseEntity.getBody();
        Assertions.assertNotNull(body);

        Assertions.assertNotNull(body.data());
        Assertions.assertTrue(body.success());
    }
}