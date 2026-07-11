package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.CreateLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.CreateLevelMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLevelControllerTest {

    @Mock
    private UseCase<CreateLevelInput, CreateLevelOutput> useCase;

    @InjectMocks
    private CreateLevelController controller;

    private UUID authAdminId;
    private AuthenticatedUser principal;
    private CreateLevelRequest mockRequest;
    private CreateLevelInput mockInput;
    private CreateLevelOutput mockOutput;
    private CreateLevelResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<CreateLevelResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        principal = new AuthenticatedUser(authAdminId, "Admin", true);
        mockRequest = mock(CreateLevelRequest.class);
        mockInput = mock(CreateLevelInput.class);
        mockOutput = mock(CreateLevelOutput.class);
        mockResponseDto = mock(CreateLevelResponse.class);

        SuccessResponse<CreateLevelResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle request, map DTOs, execute use case and return a wrapped success response")
    void shouldCreateLevelSuccessfully() {
        try (MockedStatic<CreateLevelMapper> mapperMock = mockStatic(CreateLevelMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> CreateLevelMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> CreateLevelMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<CreateLevelResponse>> response = controller.handle(principal, mockRequest);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure or business execution exceptions thrown from inside the use case context layer")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<CreateLevelMapper> mapperMock = mockStatic(CreateLevelMapper.class)) {

            mapperMock.when(() -> CreateLevelMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or invalid payload logic"));

            assertThrows(SecurityException.class, () -> controller.handle(principal, mockRequest));
        }
    }

    @Test
    @DisplayName("Should interrupt processing workflow if structural mapper validation issues trigger in the conversion layer mapping process")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<CreateLevelMapper> mapperMock = mockStatic(CreateLevelMapper.class)) {

            mapperMock.when(() -> CreateLevelMapper.toInput(authAdminId, mockRequest))
                    .thenThrow(new IllegalArgumentException("Failed to convert metadata parameters into creation models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal, mockRequest));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context pipelines if response presentation layout mapper mappings crash unexpectedly")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<CreateLevelMapper> mapperMock = mockStatic(CreateLevelMapper.class)) {

            mapperMock.when(() -> CreateLevelMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> CreateLevelMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted level tracking data model representation maps"));

            assertThrows(IllegalStateException.class, () -> controller.handle(principal, mockRequest));
        }
    }
}