package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.UpdateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.UpdateLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.UpdateLevelMapper;
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
class UpdateLevelControllerTest {

    @Mock
    private UseCase<UpdateLevelInput, UpdateLevelOutput> useCase;

    @InjectMocks
    private UpdateLevelController controller;

    private UUID authAdminId;
    private AuthenticatedUser principal;
    private UUID levelId;
    private UpdateLevelRequest mockRequest;
    private UpdateLevelInput mockInput;
    private UpdateLevelOutput mockOutput;
    private UpdateLevelResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<UpdateLevelResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        principal = new AuthenticatedUser(authAdminId, "Admin", true);
        levelId = UUID.randomUUID();
        mockRequest = mock(UpdateLevelRequest.class);
        mockInput = mock(UpdateLevelInput.class);
        mockOutput = mock(UpdateLevelOutput.class);
        mockResponseDto = mock(UpdateLevelResponse.class);

        SuccessResponse<UpdateLevelResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle modification path, map payloads, execute use case and return wrapped success response")
    void shouldUpdateLevelSuccessfully() {
        try (MockedStatic<UpdateLevelMapper> mapperMock = mockStatic(UpdateLevelMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> UpdateLevelMapper.toInput(authAdminId, levelId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> UpdateLevelMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<UpdateLevelResponse>> response = controller.handle(principal, levelId, mockRequest);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure or business exceptions directly when modification use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<UpdateLevelMapper> mapperMock = mockStatic(UpdateLevelMapper.class)) {

            mapperMock.when(() -> UpdateLevelMapper.toInput(authAdminId, levelId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or processing violation"));

            assertThrows(SecurityException.class, () -> controller.handle(principal, levelId, mockRequest));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under validation maps")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<UpdateLevelMapper> mapperMock = mockStatic(UpdateLevelMapper.class)) {

            mapperMock.when(() -> UpdateLevelMapper.toInput(authAdminId, levelId, mockRequest))
                    .thenThrow(new IllegalArgumentException("Failed to convert payload attributes into updating structural criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal, levelId, mockRequest));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<UpdateLevelMapper> mapperMock = mockStatic(UpdateLevelMapper.class)) {

            mapperMock.when(() -> UpdateLevelMapper.toInput(authAdminId, levelId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> UpdateLevelMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing target definitions"));

            assertThrows(IllegalStateException.class, () -> controller.handle(principal, levelId, mockRequest));
        }
    }
}