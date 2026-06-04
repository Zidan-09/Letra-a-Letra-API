package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.application.usecase.CreateUserUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.CreateUserRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.CreateUserResponse;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class CreateUserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private CreateUserController controller;

    @Test
    @DisplayName("should get the request to create an user and return an response correctly")
    void createUser() {
        CreateUserRequest request = new CreateUserRequest("teste@email.com", "12341234");

        CreateUserOutput output = new CreateUserOutput("id1", "random-nickname", "avatar-1", "teste@email.com");

        when(createUserUseCase
                .execute(any(CreateUserInput.class)))
                .thenReturn(output);

        ResponseEntity<SuccessResponse<CreateUserResponse>> responseEntity = controller.createUser(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        SuccessResponse<CreateUserResponse> body = responseEntity.getBody();
        assertNotNull(body);

        assertEquals(UserMessages.USER_CREATED.getMessage(), body.message());
        assertNotNull(body.data());
        Assertions.assertTrue(body.success());
    }
}