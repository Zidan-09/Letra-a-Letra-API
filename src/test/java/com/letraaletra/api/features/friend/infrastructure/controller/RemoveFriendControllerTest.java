package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RemoveFriendRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RemoveFriendMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveFriendControllerTest {

    @Mock
    private UseCase<RemoveFriendInput, Void> useCase;

    @InjectMocks
    private RemoveFriendController controller;

    private UUID mockAuthId;
    private String mockFriendId;
    private RemoveFriendRequest mockRequest;
    private RemoveFriendInput mockInput;
    private SuccessResponse<Void> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockFriendId = UUID.randomUUID().toString();

        mockRequest = mock(RemoveFriendRequest.class);
        when(mockRequest.friendId()).thenReturn(mockFriendId);

        mockInput = mock(RemoveFriendInput.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve remover o amigo com sucesso retornando status 200 OK")
    void removeFriend_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<RemoveFriendMapper> mapperMock = mockStatic(RemoveFriendMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> RemoveFriendMapper.toInput(mockAuthId.toString(), mockFriendId)).thenReturn(mockInput);

            ResponseEntity<SuccessResponse<Void>> expectedResponseEntity = ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<Void>> response = controller.handle(mockAuthId, mockRequest);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original sem interceptação local quando a execução do UseCase falhar")
    void removeFriend_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<RemoveFriendMapper> mapperMock = mockStatic(RemoveFriendMapper.class)) {

            mapperMock.when(() -> RemoveFriendMapper.toInput(mockAuthId.toString(), mockFriendId)).thenReturn(mockInput);
            doThrow(new RuntimeException("Friendship relationship not found or already broken")).when(useCase).execute(mockInput);

            assertThrows(RuntimeException.class, () -> controller.handle(mockAuthId, mockRequest));
        }
    }
}