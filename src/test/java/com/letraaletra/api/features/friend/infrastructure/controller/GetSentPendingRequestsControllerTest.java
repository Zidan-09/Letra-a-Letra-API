package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetSentPendingRequestsResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetSentPendingRequestsMapper;
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
class GetSentPendingRequestsControllerTest {

    @Mock
    private UseCase<GetSentPendingRequestsInput, GetSentPendingRequestsOutput> useCase;

    @InjectMocks
    private GetSentPendingRequestsController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private GetSentPendingRequestsInput mockInput;
    private GetSentPendingRequestsOutput mockOutput;
    private GetSentPendingRequestsResponse mockResponseDto;
    private SuccessResponse<GetSentPendingRequestsResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "User", false, false);
        mockInput = mock(GetSentPendingRequestsInput.class);
        mockOutput = mock(GetSentPendingRequestsOutput.class);
        mockResponseDto = mock(GetSentPendingRequestsResponse.class);
        mockSuccessResponse = new SuccessResponse<>(true, mockResponseDto);
    }

    @Test
    @DisplayName("Deve retornar as solicitações enviadas com sucesso (200 OK)")
    void handle_ShouldReturnSentRequests_WhenUserIsAuthenticated() {
        try (MockedStatic<GetSentPendingRequestsMapper> mapperMock = mockStatic(GetSentPendingRequestsMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetSentPendingRequestsMapper.toInput(mockAuthId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetSentPendingRequestsMapper.toResponse(mockOutput, mockAuthId)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<GetSentPendingRequestsResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetSentPendingRequestsResponse>> response = controller.handle(principal);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }
}
