package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.FindLevelResponse;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.FindLevelMapper;
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
class FindLevelControllerTest {

    @Mock
    private UseCase<FindLevelInput, FindLevelOutput> useCase;

    @InjectMocks
    private FindLevelController controller;

    private UUID levelId;
    private FindLevelInput mockInput;
    private FindLevelOutput mockOutput;
    private FindLevelResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<FindLevelResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        levelId = UUID.randomUUID();
        mockInput = mock(FindLevelInput.class);
        mockOutput = mock(FindLevelOutput.class);
        mockResponseDto = mock(FindLevelResponse.class);

        SuccessResponse<FindLevelResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully execute lookup, map the domain output and return a successful api response payload")
    void shouldFindLevelSuccessfully() {
        try (MockedStatic<FindLevelMapper> mapperMock = mockStatic(FindLevelMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> FindLevelMapper.toInput(levelId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> FindLevelMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<FindLevelResponse>> response = controller.handle(levelId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate underlying business domain or layer exceptions directly to the web dispatcher advisor")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<FindLevelMapper> mapperMock = mockStatic(FindLevelMapper.class)) {

            mapperMock.when(() -> FindLevelMapper.toInput(levelId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Level record entity completely corrupted or unavailable"));

            assertThrows(RuntimeException.class, () -> controller.handle(levelId));
        }
    }

    @Test
    @DisplayName("Should test resilience behavior when path parameters resolve to null references during conversion mapping")
    void shouldHandleExceptionWhenInputMappingProducesNullParameters() {
        try (MockedStatic<FindLevelMapper> mapperMock = mockStatic(FindLevelMapper.class)) {

            mapperMock.when(() -> FindLevelMapper.toInput(null)).thenReturn(null);
            when(useCase.execute(null)).thenThrow(new IllegalArgumentException("Level identification tracking token cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper internal logic fails during input model transformation")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<FindLevelMapper> mapperMock = mockStatic(FindLevelMapper.class)) {

            mapperMock.when(() -> FindLevelMapper.toInput(levelId))
                    .thenThrow(new IllegalArgumentException("Corrupted path variable tracking token segment format"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(levelId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution pipelines if presentation layout mapping components raise unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<FindLevelMapper> mapperMock = mockStatic(FindLevelMapper.class)) {

            mapperMock.when(() -> FindLevelMapper.toInput(levelId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> FindLevelMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Missing expected metadata properties fields on source data model maps"));

            assertThrows(IllegalStateException.class, () -> controller.handle(levelId));
        }
    }
}