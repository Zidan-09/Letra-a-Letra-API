package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.FindOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.FindOfferMapper;
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
class FindOfferControllerTest {

    @Mock
    private UseCase<FindOfferInput, FindOfferOutput> useCase;

    @InjectMocks
    private FindOfferController controller;

    private UUID offerId;
    private FindOfferInput mockInput;
    private FindOfferOutput mockOutput;
    private FindOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<FindOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        mockInput = mock(FindOfferInput.class);
        mockOutput = mock(FindOfferOutput.class);
        mockResponseDto = mock(FindOfferResponse.class);

        SuccessResponse<FindOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle find path, map payload, execute use case and return wrapped success response")
    void shouldFindOfferSuccessfully() {
        try (MockedStatic<FindOfferMapper> mapperMock = mockStatic(FindOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> FindOfferMapper.toInput(offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> FindOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<FindOfferResponse>> response = controller.handle(offerId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when find use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<FindOfferMapper> mapperMock = mockStatic(FindOfferMapper.class)) {

            mapperMock.when(() -> FindOfferMapper.toInput(offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database retrieval failure or corrupted data record"));

            assertThrows(RuntimeException.class, () -> controller.handle(offerId));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under request parameters")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<FindOfferMapper> mapperMock = mockStatic(FindOfferMapper.class)) {

            mapperMock.when(() -> FindOfferMapper.toInput(offerId))
                    .thenThrow(new IllegalArgumentException("Failed to convert identifier query attributes into structural criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(offerId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<FindOfferMapper> mapperMock = mockStatic(FindOfferMapper.class)) {

            mapperMock.when(() -> FindOfferMapper.toInput(offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> FindOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing target definitions"));

            assertThrows(IllegalStateException.class, () -> controller.handle(offerId));
        }
    }
}