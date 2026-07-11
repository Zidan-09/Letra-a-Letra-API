package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DeleteCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DeleteCosmeticMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCosmeticControllerTest {

    @Mock
    private UseCase<DeleteCosmeticInput, DeleteCosmeticOutput> useCase;

    @InjectMocks
    private DeleteCosmeticController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private String mockCosmeticId;
    private DeleteCosmeticInput mockInput;
    private DeleteCosmeticOutput mockOutput;
    private DeleteCosmeticResponse mockResponseDto;
    private SuccessResponse<DeleteCosmeticResponse> successResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "Admin", true);
        mockCosmeticId = UUID.randomUUID().toString();

        mockInput = mock(DeleteCosmeticInput.class);
        mockOutput = mock(DeleteCosmeticOutput.class);
        mockResponseDto = mock(DeleteCosmeticResponse.class);
        successResponse = new SuccessResponse<>(true, mockResponseDto);
    }

    @Test
    @DisplayName("Deve deletar cosmético com sucesso retornando 200 OK e o DTO correspondente")
    void handle_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<DeleteCosmeticMapper> mapperMock = mockStatic(DeleteCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DeleteCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DeleteCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<DeleteCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.ok(successResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<DeleteCosmeticResponse>> response = controller.handle(principal, mockCosmeticId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar exceção sem capturar caso o UseCase lance um erro")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<DeleteCosmeticMapper> mapperMock = mockStatic(DeleteCosmeticMapper.class)) {

            mapperMock.when(() -> DeleteCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Internal error or business restriction"));

            assertThrows(RuntimeException.class, () -> controller.handle(principal, mockCosmeticId));

            mapperMock.verify(() -> DeleteCosmeticMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve propagar exceção se o mapeamento de entrada falhar por parâmetros nulos ou inválidos")
    void handle_ShouldThrowException_WhenMapperToInputFails() {
        try (MockedStatic<DeleteCosmeticMapper> mapperMock = mockStatic(DeleteCosmeticMapper.class)) {

            mapperMock.when(() -> DeleteCosmeticMapper.toInput(null, null))
                    .thenThrow(new NullPointerException("Parameters cannot be null"));

            assertThrows(NullPointerException.class, () -> controller.handle(null, null));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento consistente caso a transformação de saída retorne nulo (Comportamento Desejado/Ausente)")
    void handle_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<DeleteCosmeticMapper> mapperMock = mockStatic(DeleteCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> DeleteCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> DeleteCosmeticMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<DeleteCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<DeleteCosmeticResponse>> response = controller.handle(principal, mockCosmeticId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}