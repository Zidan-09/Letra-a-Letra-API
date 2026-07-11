package com.letraaletra.api.features.shop.infrastructure.controller;

import com.letraaletra.api.features.shop.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.shop.infrastructure.presentation.dto.response.GetActiveOffersResponse;
import com.letraaletra.api.features.shop.infrastructure.presentation.mapper.GetActiveOffersMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetActiveOffersControllerTest {

    @Mock
    private UseCase<Void, GetActiveOffersOutput> useCase;

    @InjectMocks
    private GetActiveOffersController controller;

    private GetActiveOffersOutput mockOutput;
    private GetActiveOffersResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<GetActiveOffersResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        mockOutput = mock(GetActiveOffersOutput.class);
        mockResponseDto = mock(GetActiveOffersResponse.class);

        SuccessResponse<GetActiveOffersResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully retrieve active store offers, execute use case with null input and return wrapped response data")
    void shouldSuccessfullyGetActiveOffers() {
        try (MockedStatic<GetActiveOffersMapper> mapperMock = mockStatic(GetActiveOffersMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            when(useCase.execute(null)).thenReturn(mockOutput);
            mapperMock.when(() -> GetActiveOffersMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<GetActiveOffersResponse>> response = controller.handle();

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(null);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure or backend exceptions directly when the active offers fetch use case fails")
    void shouldPropagateUseCaseExceptions() {
        when(useCase.execute(null)).thenThrow(new RuntimeException("Database connection failure or internal store error"));

        assertThrows(RuntimeException.class, () -> controller.handle());
    }

    @Test
    @DisplayName("Should block pipeline processing if structural mapping exceptions trigger inside the presentation response mapper layer")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<GetActiveOffersMapper> mapperMock = mockStatic(GetActiveOffersMapper.class)) {

            when(useCase.execute(null)).thenReturn(mockOutput);
            mapperMock.when(() -> GetActiveOffersMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Failed to parse or map corrupted store product catalogs"));

            assertThrows(IllegalStateException.class, () -> controller.handle());
        }
    }

    @Test
    @DisplayName("Should propagate exceptions raised by the global ApiResponseService package context execution wrapper")
    void shouldPropagateApiResponseServiceExceptions() {
        try (MockedStatic<GetActiveOffersMapper> mapperMock = mockStatic(GetActiveOffersMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            when(useCase.execute(null)).thenReturn(mockOutput);
            mapperMock.when(() -> GetActiveOffersMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto))
                    .thenThrow(new IllegalArgumentException("Invalid presentation serialization properties mapping"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle());
        }
    }
}