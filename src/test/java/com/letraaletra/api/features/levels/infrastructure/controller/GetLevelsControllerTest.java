package com.letraaletra.api.features.levels.infrastructure.controller;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.infrastructure.presentation.mapper.GetLevelsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLevelsControllerTest {

    @Mock
    private UseCase<GetLevelsInput, GetLevelsOutput> useCase;

    @InjectMocks
    private GetLevelsController controller;

    private Pageable pageable;
    private GetLevelsInput mockInput;
    private GetLevelsOutput mockOutput;
    private PageResponse<Level> responseDto;
    private ResponseEntity<SuccessResponse<PageResponse<Level>>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);
        mockInput = mock(GetLevelsInput.class);
        mockOutput = mock(GetLevelsOutput.class);
        responseDto = new PageResponse<>(
                List.of(),
                0,
                20,
                0,
                0,
                true,
                true
        );

        SuccessResponse<PageResponse<Level>> successResponse = new SuccessResponse<>(true, responseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle pagination parameters, execute query use case and return wrapped page contents")
    void shouldGetLevelsSuccessfully() {
        try (MockedStatic<GetLevelsMapper> mapperMock = mockStatic(GetLevelsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetLevelsMapper.toInput(pageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetLevelsMapper.toResponse(mockOutput)).thenReturn(responseDto);
            apiResponseMock.when(() -> ApiResponseService.success(responseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<PageResponse<Level>>> response = controller.handle(pageable);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure or business exceptions directly when paginated lookup use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GetLevelsMapper> mapperMock = mockStatic(GetLevelsMapper.class)) {

            mapperMock.when(() -> GetLevelsMapper.toInput(pageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database query compilation failure or unparseable sorting options"));

            assertThrows(RuntimeException.class, () -> controller.handle(pageable));
        }
    }

    @Test
    @DisplayName("Should test resilience criteria when fallback pagination or empty tracking parameters resolve to null contexts")
    void shouldHandleExceptionWhenInputMappingProducesNullParameters() {
        try (MockedStatic<GetLevelsMapper> mapperMock = mockStatic(GetLevelsMapper.class)) {

            mapperMock.when(() -> GetLevelsMapper.toInput(null)).thenReturn(null);
            when(useCase.execute(null)).thenThrow(new IllegalArgumentException("Pagination bounds criteria mapping framework inputs cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under validation maps")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<GetLevelsMapper> mapperMock = mockStatic(GetLevelsMapper.class)) {

            mapperMock.when(() -> GetLevelsMapper.toInput(pageable))
                    .thenThrow(new IllegalArgumentException("Invalid property or sorting target key binding definition"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(pageable));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<GetLevelsMapper> mapperMock = mockStatic(GetLevelsMapper.class)) {

            mapperMock.when(() -> GetLevelsMapper.toInput(pageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetLevelsMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Inconsistent total structural page records count elements mapped"));

            assertThrows(IllegalStateException.class, () -> controller.handle(pageable));
        }
    }
}