package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.CancelFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.CancelFriendRequestMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
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
class CancelFriendRequestControllerTest {

    @Mock
    private UseCase<CancelFriendRequestInput, Void> useCase;

    @InjectMocks
    private CancelFriendRequestController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private UUID mockFriendId;
    private CancelFriendRequestRequest mockRequest;
    private CancelFriendRequestInput mockInput;
    private SuccessResponse<Void> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "User", false, false);
        mockFriendId = UUID.randomUUID();

        mockRequest = mock(CancelFriendRequestRequest.class);
        when(mockRequest.friendId()).thenReturn(mockFriendId);

        mockInput = mock(CancelFriendRequestInput.class);
        mockSuccessResponse = new SuccessResponse<>(true, null);
    }

    @Test
    @DisplayName("Deve cancelar a solicitação com sucesso retornando 200 OK")
    void cancelFriendRequest_ShouldReturnOk_WhenValidParametersAreProvided() {
        try (MockedStatic<CancelFriendRequestMapper> mapperMock = mockStatic(CancelFriendRequestMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> CancelFriendRequestMapper.toInput(mockAuthId, mockFriendId))
                    .thenReturn(mockInput);

            ResponseEntity<SuccessResponse<Void>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);

            apiResponseMock.when(() -> ApiResponseHandler.success(null))
                    .thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<Void>> response = controller.handle(principal, mockRequest);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção quando o UseCase falhar")
    void cancelFriendRequest_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<CancelFriendRequestMapper> mapperMock = mockStatic(CancelFriendRequestMapper.class)) {

            mapperMock.when(() -> CancelFriendRequestMapper.toInput(mockAuthId, mockFriendId))
                    .thenReturn(mockInput);

            doThrow(new RuntimeException("cannot cancel")).when(useCase).execute(mockInput);

            assertThrows(RuntimeException.class, () -> controller.handle(principal, mockRequest));
        }
    }
}
