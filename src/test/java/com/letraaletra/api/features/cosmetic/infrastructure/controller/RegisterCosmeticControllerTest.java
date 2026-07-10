package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.RegisterCosmeticMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCosmeticControllerTest {

    @Mock
    private UseCase<RegisterCosmeticInput, RegisterCosmeticOutput> useCase;

    @InjectMocks
    private RegisterCosmeticController controller;

    private UUID mockAuthId;
    private RegisterCosmeticRequest mockRequest;
    private RegisterCosmeticInput mockInput;
    private RegisterCosmeticOutput mockOutput;
    private RegisterCosmeticResponse mockResponseDto;
    private SuccessResponse<RegisterCosmeticResponse> successResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockRequest = mock(RegisterCosmeticRequest.class);
        mockInput = mock(RegisterCosmeticInput.class);
        mockOutput = mock(RegisterCosmeticOutput.class);
        mockResponseDto = mock(RegisterCosmeticResponse.class);
        successResponse = new SuccessResponse<>(true, mockResponseDto);
    }

    @Test
    @DisplayName("Deve registrar um cosmético com sucesso retornando status 200 OK e o payload mapeado")
    void registerCosmetic_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<RegisterCosmeticMapper> mapperMock = mockStatic(RegisterCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> RegisterCosmeticMapper.toInput(mockAuthId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> RegisterCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.ok(successResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> response = controller.registerCosmetic(mockAuthId, mockRequest);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção sem tratamento local caso a execução do UseCase falhe")
    void registerCosmetic_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<RegisterCosmeticMapper> mapperMock = mockStatic(RegisterCosmeticMapper.class)) {

            mapperMock.when(() -> RegisterCosmeticMapper.toInput(mockAuthId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Validation, conversion or storage error"));

            assertThrows(RuntimeException.class, () -> controller.registerCosmetic(mockAuthId, mockRequest));

            mapperMock.verify(() -> RegisterCosmeticMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve falhar se a transformação do DTO de requisição HTTP para o Input do caso de uso estourar erro")
    void registerCosmetic_ShouldThrowException_WhenMapperToInputFails() {
        try (MockedStatic<RegisterCosmeticMapper> mapperMock = mockStatic(RegisterCosmeticMapper.class)) {

            mapperMock.when(() -> RegisterCosmeticMapper.toInput(null, null))
                    .thenThrow(new NullPointerException("Request and context parameters cannot be missing"));

            assertThrows(NullPointerException.class, () -> controller.registerCosmetic(null, null));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento estrutural correto se o Mapper de resposta retornar nulo (Comportamento Desejado/Ausente)")
    void registerCosmetic_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<RegisterCosmeticMapper> mapperMock = mockStatic(RegisterCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> RegisterCosmeticMapper.toInput(mockAuthId, mockRequest)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> RegisterCosmeticMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<RegisterCosmeticResponse>> response = controller.registerCosmetic(mockAuthId, mockRequest);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}