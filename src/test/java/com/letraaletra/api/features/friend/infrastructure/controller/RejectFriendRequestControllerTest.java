package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RejectFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RejectFriendRequestMapper;
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
class RejectFriendRequestControllerTest {

    @Mock
    private UseCase<RejectFriendRequestInput, Void> useCase;

    @InjectMocks
    private RejectFriendRequestController controller;

    private UUID mockAuthId;
    private String mockFriendId;
    private RejectFriendRequestRequest mockRequest;
    private RejectFriendRequestInput mockInput;
    private SuccessResponse<Void> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockFriendId = UUID.randomUUID().toString();

        mockRequest = mock(RejectFriendRequestRequest.class);
        when(mockRequest.friendId()).thenReturn(mockFriendId);

        mockInput = mock(RejectFriendRequestInput.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve rejeitar a solicitação com sucesso retornando 200 OK")
    void rejectFriendRequest_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<RejectFriendRequestMapper> mapperMock = mockStatic(RejectFriendRequestMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> RejectFriendRequestMapper.toInput(mockAuthId.toString(), mockFriendId))
                    .thenReturn(mockInput);

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
    @DisplayName("Deve propagar a exceção sem interceptar caso a execução do UseCase falhe por regra de domínio")
    void rejectFriendRequest_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<RejectFriendRequestMapper> mapperMock = mockStatic(RejectFriendRequestMapper.class)) {

            mapperMock.when(() -> RejectFriendRequestMapper.toInput(mockAuthId.toString(), mockFriendId))
                    .thenReturn(mockInput);

            doThrow(new RuntimeException("Friend request not found or condition unmet"))
                    .when(useCase).execute(mockInput);

            assertThrows(RuntimeException.class, () -> controller.handle(mockAuthId, mockRequest));
        }
    }
}