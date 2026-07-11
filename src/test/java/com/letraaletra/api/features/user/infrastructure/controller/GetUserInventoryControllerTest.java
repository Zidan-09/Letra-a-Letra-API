package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetUserInventoryResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetUserInventoryMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserInventoryControllerTest {

    @Mock
    private UseCase<GetUserInventoryInput, GetUserInventoryOutput> useCase;

    @InjectMocks
    private GetUserInventoryController controller;

    private UUID authUserId;
    private GetUserInventoryInput mockInput;
    private GetUserInventoryOutput mockOutput;
    private GetUserInventoryResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<GetUserInventoryResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        mockInput = mock(GetUserInventoryInput.class);
        mockOutput = mock(GetUserInventoryOutput.class);
        mockResponseDto = mock(GetUserInventoryResponse.class);

        SuccessResponse<GetUserInventoryResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully process request, fetch inventory details and return success wrapper response")
    void shouldSuccessfullyGetUserInventory() {
        try (MockedStatic<GetUserInventoryMapper> mapperMock = mockStatic(GetUserInventoryMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetUserInventoryMapper.toInput(authUserId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetUserInventoryMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<GetUserInventoryResponse>> response = controller.handle(authUserId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure/business runtime exceptions safely to the global controller advisor")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GetUserInventoryMapper> mapperMock = mockStatic(GetUserInventoryMapper.class)) {

            mapperMock.when(() -> GetUserInventoryMapper.toInput(authUserId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database timeout or entity not found"));

            assertThrows(RuntimeException.class, () -> controller.handle(authUserId));
        }
    }

    @Test
    @DisplayName("Should propagate exceptions thrown during request mapping phase when arguments or contexts are invalid")
    void shouldPropagateMapperExceptions() {
        try (MockedStatic<GetUserInventoryMapper> mapperMock = mockStatic(GetUserInventoryMapper.class)) {

            mapperMock.when(() -> GetUserInventoryMapper.toInput(authUserId))
                    .thenThrow(new IllegalArgumentException("Failed to construct input parameter object"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(authUserId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should handle edge case gracefully when authentication principal reference evaluation evaluates to null")
    void shouldHandleNullAuthenticationPrincipalContext() {
        try (MockedStatic<GetUserInventoryMapper> mapperMock = mockStatic(GetUserInventoryMapper.class)) {

            mapperMock.when(() -> GetUserInventoryMapper.toInput(null)).thenReturn(null);
            when(useCase.execute(null)).thenThrow(new IllegalArgumentException("Authentication required"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null));
        }
    }
}