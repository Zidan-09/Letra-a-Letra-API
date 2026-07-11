package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.GetOffersResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.GetOffersMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOffersControllerTest {

    @Mock
    private UseCase<GetOffersInput, GetOffersOutput> useCase;

    @InjectMocks
    private GetOffersController controller;

    private Pageable mockPageable;
    private GetOffersInput mockInput;
    private GetOffersOutput mockOutput;
    private GetOffersResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<GetOffersResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        mockPageable = mock(Pageable.class);
        mockInput = mock(GetOffersInput.class);
        mockOutput = mock(GetOffersOutput.class);
        mockResponseDto = mock(GetOffersResponse.class);

        SuccessResponse<GetOffersResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle get offers query path, map pagination criteria, execute use case and return wrapped success response")
    void shouldGetOffersSuccessfully() {
        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetOffersMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<GetOffersResponse>> response = controller.handle(mockPageable);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when get offers retrieval execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database pagination failure or search execution timeout"));

            assertThrows(RuntimeException.class, () -> controller.handle(mockPageable));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under pagination criteria parameters")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable))
                    .thenThrow(new IllegalArgumentException("Failed to convert pagination query attributes into structural search criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(mockPageable));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation collection wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetOffersMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing bulk layout definitions"));

            assertThrows(IllegalStateException.class, () -> controller.handle(mockPageable));
        }
    }
}