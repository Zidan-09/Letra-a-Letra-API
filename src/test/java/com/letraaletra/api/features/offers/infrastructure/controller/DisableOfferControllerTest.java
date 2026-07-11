package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.DisableOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.DisableOfferMapper;
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
class DisableOfferControllerTest {

    @Mock
    private UseCase<DisableOfferInput, DisableOfferOutput> useCase;

    @InjectMocks
    private DisableOfferController controller;

    private UUID authAdminId;
    private AuthenticatedUser principal;
    private UUID offerIdStr;
    private DisableOfferInput mockInput;
    private DisableOfferOutput mockOutput;
    private DisableOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<DisableOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        principal = new AuthenticatedUser(authAdminId, "admin", true);
        offerIdStr = UUID.randomUUID();
        mockInput = mock(DisableOfferInput.class);
        mockOutput = mock(DisableOfferOutput.class);
        mockResponseDto = mock(DisableOfferResponse.class);

        SuccessResponse<DisableOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle modification patch path, map payloads, execute use case and return wrapped success response")
    void shouldDisableOfferSuccessfully() {
        try (MockedStatic<DisableOfferMapper> mapperMock = mockStatic(DisableOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DisableOfferMapper.toInput(authAdminId, offerIdStr)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DisableOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<DisableOfferResponse>> response = controller.disableOffer(principal, offerIdStr);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when disabling use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<DisableOfferMapper> mapperMock = mockStatic(DisableOfferMapper.class)) {

            mapperMock.when(() -> DisableOfferMapper.toInput(authAdminId, offerIdStr)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or processing violation"));

            assertThrows(SecurityException.class, () -> controller.disableOffer(principal, offerIdStr));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under structural data blocks")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<DisableOfferMapper> mapperMock = mockStatic(DisableOfferMapper.class)) {

            mapperMock.when(() -> DisableOfferMapper.toInput(authAdminId, offerIdStr))
                    .thenThrow(new IllegalArgumentException("Failed to convert payload attributes into disabling structural criteria models"));

            assertThrows(IllegalArgumentException.class, () -> controller.disableOffer(principal, offerIdStr));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<DisableOfferMapper> mapperMock = mockStatic(DisableOfferMapper.class)) {

            mapperMock.when(() -> DisableOfferMapper.toInput(authAdminId, offerIdStr)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DisableOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing target definitions"));

            assertThrows(IllegalStateException.class, () -> controller.disableOffer(principal, offerIdStr));
        }
    }
}