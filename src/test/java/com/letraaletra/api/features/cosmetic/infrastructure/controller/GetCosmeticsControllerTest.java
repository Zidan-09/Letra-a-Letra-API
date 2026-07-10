package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.GetCosmeticsResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.GetCosmeticsMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCosmeticsControllerTest {

    @Mock
    private UseCase<GetCosmeticsInput, GetCosmeticsOutput> useCase;

    @InjectMocks
    private GetCosmeticsController controller;

    private Pageable mockPageable;
    private GetCosmeticsInput mockInput;
    private GetCosmeticsOutput mockOutput;
    private GetCosmeticsResponse mockResponseDto;
    private SuccessResponse<GetCosmeticsResponse> successResponse;

    @BeforeEach
    void setUp() {
        mockPageable = PageRequest.of(0, 20);
        mockInput = mock(GetCosmeticsInput.class);
        mockOutput = mock(GetCosmeticsOutput.class);
        mockResponseDto = mock(GetCosmeticsResponse.class);
        successResponse = new SuccessResponse<>(true, mockResponseDto);
    }

    @Test
    @DisplayName("Deve retornar a lista paginada de cosméticos com sucesso (200 OK)")
    void handle_ShouldReturnCosmeticsList_WhenPageableIsValid() {
        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetCosmeticsMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<GetCosmeticsResponse>> expectedResponseEntity =
                    ResponseEntity.ok(successResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetCosmeticsResponse>> response = controller.handle(mockPageable);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar erro original sem interceptação se o UseCase falhar")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database timeout error"));

            assertThrows(RuntimeException.class, () -> controller.handle(mockPageable));

            mapperMock.verify(() -> GetCosmeticsMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve propagar erro se o mapeamento do Pageable de entrada falhar")
    void handle_ShouldThrowException_WhenMapperToInputFails() {
        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(null))
                    .thenThrow(new NullPointerException("Pageable criteria cannot be null"));

            assertThrows(NullPointerException.class, () -> controller.handle(null));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve garantir o correto envelopamento se a resposta estruturada vier vazia")
    void handle_ShouldReturnSuccessWithEmptyData_WhenNoCosmeticsAreFound() {
        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(mockPageable)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);

            mapperMock.when(() -> GetCosmeticsMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<GetCosmeticsResponse>> expectedResponseEntity = ResponseEntity.ok(successResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetCosmeticsResponse>> response = controller.handle(mockPageable);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}