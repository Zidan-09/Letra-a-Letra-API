package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendPendingRequestsResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendPendingRequestsMapper;
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
class GetFriendPendingRequestsControllerTest {

    @Mock
    private UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> useCase;

    @InjectMocks
    private GetFriendPendingRequestsController controller;

    private UUID mockAuthId;
    private GetFriendPendingRequestsInput mockInput;
    private GetFriendPendingRequestsOutput mockOutput;
    private GetFriendPendingRequestsResponse mockResponseDto;
    private SuccessResponse<GetFriendPendingRequestsResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockInput = mock(GetFriendPendingRequestsInput.class);
        mockOutput = mock(GetFriendPendingRequestsOutput.class);
        mockResponseDto = mock(GetFriendPendingRequestsResponse.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve retornar a lista de solicitações pendentes com sucesso (200 OK) quando o usuário estiver autenticado")
    void handle_ShouldReturnPendingRequests_WhenUserIsAuthenticated() {
        try (MockedStatic<GetFriendPendingRequestsMapper> mapperMock = mockStatic(GetFriendPendingRequestsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetFriendPendingRequestsMapper.toInput(mockAuthId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetFriendPendingRequestsMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> response = controller.handle(mockAuthId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar erro original sem interceptação local caso o UseCase falhe")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<GetFriendPendingRequestsMapper> mapperMock = mockStatic(GetFriendPendingRequestsMapper.class)) {

            mapperMock.when(() -> GetFriendPendingRequestsMapper.toInput(mockAuthId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database timeout or communication failure"));

            assertThrows(RuntimeException.class, () -> controller.handle(mockAuthId));

            mapperMock.verify(() -> GetFriendPendingRequestsMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original se a conversão do Mapper de entrada falhar")
    void handle_ShouldThrowException_WhenMapperToInputThrowsException() {
        try (MockedStatic<GetFriendPendingRequestsMapper> mapperMock = mockStatic(GetFriendPendingRequestsMapper.class)) {

            mapperMock.when(() -> GetFriendPendingRequestsMapper.toInput(null))
                    .thenThrow(new NullPointerException("Authentication principal context cannot be null"));

            assertThrows(NullPointerException.class, () -> controller.handle(null));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento estrutural correto se o Mapper de resposta retornar nulo (Comportamento Desejado/Ausente)")
    void handle_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<GetFriendPendingRequestsMapper> mapperMock = mockStatic(GetFriendPendingRequestsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetFriendPendingRequestsMapper.toInput(mockAuthId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetFriendPendingRequestsMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> response = controller.handle(mockAuthId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}