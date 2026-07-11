package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.SendFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.SendFriendRequestResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.SendFriendRequestMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
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
class SendFriendRequestControllerTest {

    @Mock
    private UseCase<SendFriendRequestInput, SendFriendRequestOutput> useCase;

    @InjectMocks
    private SendFriendRequestController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private String mockFriendId;
    private SendFriendRequestRequest mockRequest;
    private SendFriendRequestInput mockInput;
    private SendFriendRequestOutput mockOutput;
    private SendFriendRequestResponse mockResponseDto;
    private SuccessResponse<SendFriendRequestResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "Admin", false);
        mockFriendId = UUID.randomUUID().toString();

        mockRequest = mock(SendFriendRequestRequest.class);
        when(mockRequest.friendId()).thenReturn(mockFriendId);

        mockInput = mock(SendFriendRequestInput.class);
        mockOutput = mock(SendFriendRequestOutput.class);
        mockResponseDto = mock(SendFriendRequestResponse.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve enviar uma solicitação de amizade com sucesso retornando status 200 OK e o payload envelopado")
    void sendFriendRequest_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<SendFriendRequestMapper> mapperMock = mockStatic(SendFriendRequestMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> SendFriendRequestMapper.toInput(mockAuthId, mockFriendId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> SendFriendRequestMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<SendFriendRequestResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<SendFriendRequestResponse>> response = controller.handle(principal, mockRequest);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original sem interceptação local quando a execução do UseCase falhar")
    void sendFriendRequest_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<SendFriendRequestMapper> mapperMock = mockStatic(SendFriendRequestMapper.class)) {

            mapperMock.when(() -> SendFriendRequestMapper.toInput(mockAuthId, mockFriendId)).thenReturn(mockInput);
            doThrow(new RuntimeException("Users are already friends or request is pending")).when(useCase).execute(mockInput);

            assertThrows(RuntimeException.class, () -> controller.handle(principal, mockRequest));

            mapperMock.verify(() -> SendFriendRequestMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento estrutural resiliente caso o Mapper de resposta retorne nulo (Comportamento Desejado/Ausente)")
    void sendFriendRequest_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<SendFriendRequestMapper> mapperMock = mockStatic(SendFriendRequestMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> SendFriendRequestMapper.toInput(mockAuthId, mockFriendId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> SendFriendRequestMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<SendFriendRequestResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<SendFriendRequestResponse>> response = controller.handle(principal, mockRequest);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}