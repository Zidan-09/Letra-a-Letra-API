package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.AcceptFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.AcceptFriendRequestMapper;
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
class AcceptFriendRequestControllerTest {

    @Mock
    private UseCase<AcceptFriendRequestInput, Void> useCase;

    @InjectMocks
    private AcceptFriendRequestController controller;

    private UUID mockAuthId;
    private String mockFriendId;
    private AcceptFriendRequestRequest mockRequest;
    private AcceptFriendRequestInput mockInput;
    private SuccessResponse<Void> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockFriendId = UUID.randomUUID().toString();

        mockRequest = mock(AcceptFriendRequestRequest.class);
        when(mockRequest.friendId()).thenReturn(mockFriendId);

        mockInput = mock(AcceptFriendRequestInput.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve aceitar a solicitação com sucesso retornando 204 No Content")
    void acceptFriendRequest_ShouldReturnNoContent_WhenValidParametersAreProvided() {
        try (MockedStatic<AcceptFriendRequestMapper> mapperMock = mockStatic(AcceptFriendRequestMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> AcceptFriendRequestMapper.toInput(mockAuthId.toString(), mockFriendId))
                    .thenReturn(mockInput);

            ResponseEntity<SuccessResponse<Void>> expectedResponseEntity =
                    ResponseEntity.status(HttpStatus.NO_CONTENT).body(mockSuccessResponse);

            apiResponseMock.when(() -> ApiResponseService.success(null, HttpStatus.NO_CONTENT))
                    .thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<Void>> response = controller.acceptFriendRequest(mockAuthId, mockRequest);

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção sem interceptar caso a execução do UseCase falhe por regra de domínio")
    void acceptFriendRequest_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<AcceptFriendRequestMapper> mapperMock = mockStatic(AcceptFriendRequestMapper.class)) {

            mapperMock.when(() -> AcceptFriendRequestMapper.toInput(mockAuthId.toString(), mockFriendId))
                    .thenReturn(mockInput);

            doThrow(new RuntimeException("Friend request not found or unauthorized"))
                    .when(useCase).execute(mockInput);

            assertThrows(RuntimeException.class, () -> controller.acceptFriendRequest(mockAuthId, mockRequest));
        }
    }
}