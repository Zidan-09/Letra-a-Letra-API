package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DisableCosmeticMapper;
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
class DisableCosmeticControllerTest {

    @Mock
    private UseCase<DisableCosmeticInput, DisableCosmeticOutput> useCase;

    @InjectMocks
    private DisableCosmeticController controller;

    private UUID mockAuthId;
    private String mockCosmeticId;
    private DisableCosmeticInput mockInput;
    private DisableCosmeticOutput mockOutput;
    private DisableCosmeticResponse mockResponseDto;
    private SuccessResponse<DisableCosmeticResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockCosmeticId = UUID.randomUUID().toString();

        mockInput = mock(DisableCosmeticInput.class);
        mockOutput = mock(DisableCosmeticOutput.class);
        mockResponseDto = mock(DisableCosmeticResponse.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve desabilitar o cosmético com sucesso retornando 200 OK e o payload envelopado")
    void handle_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<DisableCosmeticMapper> mapperMock = mockStatic(DisableCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DisableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DisableCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<DisableCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<DisableCosmeticResponse>> response = controller.handle(mockAuthId, mockCosmeticId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original quando o UseCase falhar por regra de negócio")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<DisableCosmeticMapper> mapperMock = mockStatic(DisableCosmeticMapper.class)) {

            mapperMock.when(() -> DisableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Cosmetic is already disabled or not found"));

            assertThrows(RuntimeException.class, () -> controller.handle(mockAuthId, mockCosmeticId));

            mapperMock.verify(() -> DisableCosmeticMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve falhar e propagar exceção se a transformação do Mapper de entrada falhar por dados inconsistentes")
    void handle_ShouldThrowException_WhenMapperToInputFails() {
        try (MockedStatic<DisableCosmeticMapper> mapperMock = mockStatic(DisableCosmeticMapper.class)) {

            mapperMock.when(() -> DisableCosmeticMapper.toInput(null, null))
                    .thenThrow(new IllegalArgumentException("Invalid UUID string or null context"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null, null));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve assegurar robustez estrutural se o Mapper de saída retornar nulo (Comportamento Desejado/Ausente)")
    void handle_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<DisableCosmeticMapper> mapperMock = mockStatic(DisableCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DisableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DisableCosmeticMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<DisableCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<DisableCosmeticResponse>> response = controller.handle(mockAuthId, mockCosmeticId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}