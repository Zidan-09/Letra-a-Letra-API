package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.DeleteOfferInput;
import com.letraaletra.api.features.offers.application.output.DeleteOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DeleteOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.DeleteOfferMapper;
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
class DeleteOfferControllerTest {

    @Mock
    private UseCase<DeleteOfferInput, DeleteOfferOutput> useCase;

    @InjectMocks
    private DeleteOfferController controller;

    private UUID authAdminId;
    private UUID offerId;
    private DeleteOfferInput mockInput;
    private DeleteOfferOutput mockOutput;
    private DeleteOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<DeleteOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        mockInput = mock(DeleteOfferInput.class);
        mockOutput = mock(DeleteOfferOutput.class);
        mockResponseDto = mock(DeleteOfferResponse.class);

        SuccessResponse<DeleteOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle deletion path, map payloads, execute use case and return wrapped success response")
    void shouldDeleteOfferSuccessfully() {
        try (MockedStatic<DeleteOfferMapper> mapperMock = mockStatic(DeleteOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DeleteOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DeleteOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<DeleteOfferResponse>> response = controller.handle(authAdminId, offerId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when deletion use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<DeleteOfferMapper> mapperMock = mockStatic(DeleteOfferMapper.class)) {

            mapperMock.when(() -> DeleteOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or processing violation"));

            assertThrows(SecurityException.class, () -> controller.handle(authAdminId, offerId));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under structural data blocks")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<DeleteOfferMapper> mapperMock = mockStatic(DeleteOfferMapper.class)) {

            mapperMock.when(() -> DeleteOfferMapper.toInput(authAdminId, offerId))
                    .thenThrow(new IllegalArgumentException("Failed to convert payload attributes into deleting structural criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(authAdminId, offerId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<DeleteOfferMapper> mapperMock = mockStatic(DeleteOfferMapper.class)) {

            mapperMock.when(() -> DeleteOfferMapper.toInput(authAdminId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DeleteOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing target definitions"));

            assertThrows(IllegalStateException.class, () -> controller.handle(authAdminId, offerId));
        }
    }
}