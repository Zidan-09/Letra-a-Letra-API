package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.EnableOfferInput;
import com.letraaletra.api.features.offers.application.output.EnableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.EnableOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.EnableOfferMapper;
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
class EnableOfferControllerTest {

    @Mock
    private UseCase<EnableOfferInput, EnableOfferOutput> useCase;

    @InjectMocks
    private EnableOfferController controller;

    private UUID authAdminId;
    private UUID offerId;
    private EnableOfferInput mockInput;
    private EnableOfferOutput mockOutput;
    private EnableOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<EnableOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        mockInput = mock(EnableOfferInput.class);
        mockOutput = mock(EnableOfferOutput.class);
        mockResponseDto = mock(EnableOfferResponse.class);

        SuccessResponse<EnableOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle enabling patch path, map payloads, execute use case and return wrapped success response")
    void shouldEnableOfferSuccessfully() {
        try (MockedStatic<EnableOfferMapper> mapperMock = mockStatic(EnableOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> EnableOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> EnableOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<EnableOfferResponse>> response = controller.handle(authAdminId, offerId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when enabling use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<EnableOfferMapper> mapperMock = mockStatic(EnableOfferMapper.class)) {

            mapperMock.when(() -> EnableOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or processing violation"));

            assertThrows(SecurityException.class, () -> controller.handle(authAdminId, offerId));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under data mapping layers")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<EnableOfferMapper> mapperMock = mockStatic(EnableOfferMapper.class)) { // Corrigido para mockStatic genérico se necessário, usando EnableOfferMapper

            mapperMock.when(() -> EnableOfferMapper.toInput(authAdminId, offerId))
                    .thenThrow(new IllegalArgumentException("Failed to convert payload attributes into enabling structural criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(authAdminId, offerId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<EnableOfferMapper> mapperMock = mockStatic(EnableOfferMapper.class)) {

            mapperMock.when(() -> EnableOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> EnableOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing target definitions"));

            assertThrows(IllegalStateException.class, () -> controller.handle(authAdminId, offerId));
        }
    }
}