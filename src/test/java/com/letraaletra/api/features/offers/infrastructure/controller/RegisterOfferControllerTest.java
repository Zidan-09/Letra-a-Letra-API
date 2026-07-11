package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.request.RegisterOfferRequest;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.RegisterOfferResponse;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.RegisterOfferMapper;
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
class RegisterOfferControllerTest {

    @Mock
    private UseCase<RegisterOfferInput, RegisterOfferOutput> useCase;

    @InjectMocks
    private RegisterOfferController controller;

    private UUID authAdminId;
    private AuthenticatedUser principal;
    private RegisterOfferRequest mockRequest;
    private RegisterOfferInput mockInput;
    private RegisterOfferOutput mockOutput;
    private RegisterOfferResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<RegisterOfferResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminId = UUID.randomUUID();
        principal = new AuthenticatedUser(authAdminId, "Admin", true);
        mockRequest = mock(RegisterOfferRequest.class);
        mockInput = mock(RegisterOfferInput.class);
        mockOutput = mock(RegisterOfferOutput.class);
        mockResponseDto = mock(RegisterOfferResponse.class);

        SuccessResponse<RegisterOfferResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle register path, map payloads, execute use case and return wrapped success response")
    void shouldRegisterOfferSuccessfully() {
        try (MockedStatic<RegisterOfferMapper> mapperMock = mockStatic(RegisterOfferMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> RegisterOfferMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> RegisterOfferMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<RegisterOfferResponse>> response = controller.registerOffer(principal, mockRequest);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions directly when registration use case execution crashes")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<RegisterOfferMapper> mapperMock = mockStatic(RegisterOfferMapper.class)) {

            mapperMock.when(() -> RegisterOfferMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new SecurityException("Unauthorized credentials profile or structural constraint violation"));

            assertThrows(SecurityException.class, () -> controller.registerOffer(principal, mockRequest));
        }
    }

    @Test
    @DisplayName("Should interrupt processing flow if structural mapper transformations fail unexpectedly under request parameters")
    void shouldPropagateInputMapperExceptions() {
        try (MockedStatic<RegisterOfferMapper> mapperMock = mockStatic(RegisterOfferMapper.class)) {

            mapperMock.when(() -> RegisterOfferMapper.toInput(authAdminId, mockRequest))
                    .thenThrow(new IllegalArgumentException("Failed to convert request body data into registration structural input models"));

            assertThrows(IllegalArgumentException.class, () -> controller.registerOffer(principal, mockRequest));
            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should fail handling execution context gracefully if presentation wrapper mapping layers throw unexpected errors")
    void shouldPropagateResponseMapperExceptions() {
        try (MockedStatic<RegisterOfferMapper> mapperMock = mockStatic(RegisterOfferMapper.class)) {

            mapperMock.when(() -> RegisterOfferMapper.toInput(authAdminId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> RegisterOfferMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Corrupted presentation data layout mappings or missing serialization parameters"));

            assertThrows(IllegalStateException.class, () -> controller.registerOffer(principal, mockRequest));
        }
    }
}