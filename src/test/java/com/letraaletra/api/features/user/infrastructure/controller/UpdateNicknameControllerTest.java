package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.UpdateNicknameInput;
import com.letraaletra.api.features.user.application.output.UpdateNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.UpdateNicknameUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.UpdateNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.UpdateNicknameResponse;
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

@ExtendWith(MockitoExtension.class)
class UpdateNicknameControllerTest {
    @Mock
    private UpdateNicknameUseCase updateNicknameUseCase;

    @InjectMocks
    private UpdateNicknameController controller;

    @Test
    @DisplayName("should get the request to update the nickname and return an response correctly")
    void updateNickname() {
        UpdateNicknameRequest request = new UpdateNicknameRequest("nickname-test-123");

        UpdateNicknameOutput output = new UpdateNicknameOutput("nickname-test-123");

        Mockito.when(updateNicknameUseCase.execute(Mockito.any(UpdateNicknameInput.class)))
                .thenReturn(output);

        ResponseEntity<SuccessResponse<UpdateNicknameResponse>> responseEntity = controller.updateNickname(request, UUID.randomUUID().toString());

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        SuccessResponse<UpdateNicknameResponse> body = responseEntity.getBody();
        Assertions.assertNotNull(body);

        Assertions.assertEquals(UserMessages.NICKNAME_SETTER.getMessage(), body.message());
        Assertions.assertNotNull(body.data());
        Assertions.assertTrue(body.success());
    }
}