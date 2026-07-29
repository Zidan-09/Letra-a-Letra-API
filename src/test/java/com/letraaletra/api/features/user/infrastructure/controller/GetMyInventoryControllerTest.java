package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetMyInventoryInput;
import com.letraaletra.api.features.user.application.output.GetMyInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetMyInventoryResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetMyInventoryMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMyInventoryControllerTest {

    @Mock
    private UseCase<GetMyInventoryInput, GetMyInventoryOutput> useCase;

    @InjectMocks
    private GetMyInventoryController controller;

    private UUID authUserId;
    private AuthenticatedUser principal;
    private GetMyInventoryInput mockInput;
    private GetMyInventoryOutput mockOutput;
    private GetMyInventoryResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<GetMyInventoryResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        principal = new AuthenticatedUser(authUserId, "User", false);
        mockInput = mock(GetMyInventoryInput.class);
        mockOutput = mock(GetMyInventoryOutput.class);
        mockResponseDto = mock(GetMyInventoryResponse.class);

        SuccessResponse<GetMyInventoryResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully process request, fetch inventory details and return success wrapper response")
    void shouldSuccessfullyGetUserInventory() {
        try (MockedStatic<GetMyInventoryMapper> mapperMock = mockStatic(GetMyInventoryMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetMyInventoryMapper.toInput(authUserId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetMyInventoryMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<GetMyInventoryResponse>> response = controller.handle(principal);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure/business runtime exceptions safely to the global controller advisor")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GetMyInventoryMapper> mapperMock = mockStatic(GetMyInventoryMapper.class)) {

            mapperMock.when(() -> GetMyInventoryMapper.toInput(authUserId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database timeout or entity not found"));

            assertThrows(RuntimeException.class, () -> controller.handle(principal));
        }
    }

    @Test
    @DisplayName("Should propagate exceptions thrown during request mapping phase when arguments or contexts are invalid")
    void shouldPropagateMapperExceptions() {
        try (MockedStatic<GetMyInventoryMapper> mapperMock = mockStatic(GetMyInventoryMapper.class)) {

            mapperMock.when(() -> GetMyInventoryMapper.toInput(authUserId))
                    .thenThrow(new IllegalArgumentException("Failed to construct input parameter object"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal));
            verifyNoInteractions(useCase);
        }
    }
}