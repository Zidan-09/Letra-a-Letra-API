package com.letraaletra.api.features.shop.infrastructure.controller;

import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.infrastructure.presentation.dto.response.BuyStoreOfferResponse;
import com.letraaletra.api.features.shop.infrastructure.presentation.mapper.BuyStoreOfferMapper;
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
class BuyStoreOfferControllerTest {

    @Mock
    private UseCase<BuyOfferInput, BuyOfferOutput> useCase;

    @InjectMocks
    private BuyStoreOfferController controller;

    private UUID authUserId;
    private UUID offerId;
    private BuyOfferInput mockInput;
    private BuyOfferOutput mockOutput;
    private BuyStoreOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<BuyStoreOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        mockInput = mock(BuyOfferInput.class);
        mockOutput = mock(BuyOfferOutput.class);
        mockResponseDto = mock(BuyStoreOfferResponse.class);

        SuccessResponse<BuyStoreOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully purchase a store offer, execute use case and return wrapped response data")
    void shouldSuccessfullyBuyStoreOffer() {
        try (MockedStatic<BuyStoreOfferMapper> mapperMock = mockStatic(BuyStoreOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> BuyStoreOfferMapper.toInput(authUserId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> BuyStoreOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<BuyStoreOfferResponse>> response = controller.handle(authUserId, offerId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business layer exceptions directly when purchase use case execution fails")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<BuyStoreOfferMapper> mapperMock = mockStatic(BuyStoreOfferMapper.class)) {

            mapperMock.when(() -> BuyStoreOfferMapper.toInput(authUserId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Insufficient funds or expired offer"));

            assertThrows(RuntimeException.class, () -> controller.handle(authUserId, offerId));
        }
    }

    @Test
    @DisplayName("Should handle resilience scenario when controller parameters or mapped payload elements resolve to null values")
    void shouldHandleExceptionWhenInputMappingProducesNullParameters() {
        try (MockedStatic<BuyStoreOfferMapper> mapperMock = mockStatic(BuyStoreOfferMapper.class)) {

            mapperMock.when(() -> BuyStoreOfferMapper.toInput(null, null)).thenReturn(null);
            when(useCase.execute(null)).thenThrow(new IllegalArgumentException("Purchase parameters cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null, null));
        }
    }

    @Test
    @DisplayName("Should block pipeline processing if structural mapping exceptions trigger inside the input request mapper layer")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<BuyStoreOfferMapper> mapperMock = mockStatic(BuyStoreOfferMapper.class)) {

            mapperMock.when(() -> BuyStoreOfferMapper.toInput(authUserId, offerId))
                    .thenThrow(new IllegalArgumentException("Invalid input properties tracking identifiers"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(authUserId, offerId));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution gracefully if presentation data response mapping crashes unexpectedly")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<BuyStoreOfferMapper> mapperMock = mockStatic(BuyStoreOfferMapper.class)) {

            mapperMock.when(() -> BuyStoreOfferMapper.toInput(authUserId, offerId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> BuyStoreOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Incomplete transaction receipt mapping structure"));

            assertThrows(IllegalStateException.class, () -> controller.handle(authUserId, offerId));
        }
    }
}