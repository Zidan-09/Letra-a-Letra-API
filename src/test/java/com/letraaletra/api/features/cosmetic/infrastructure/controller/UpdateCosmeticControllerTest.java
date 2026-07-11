package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.UpdateCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.UpdateCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.UpdateCosmeticMapper;
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
class UpdateCosmeticControllerTest {

    @Mock
    private UseCase<UpdateCosmeticInput, UpdateCosmeticOutput> useCase;

    @InjectMocks
    private UpdateCosmeticController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private String mockCosmeticId;
    private UpdateCosmeticRequest mockRequest;
    private UpdateCosmeticInput mockInput;
    private UpdateCosmeticOutput mockOutput;
    private UpdateCosmeticResponse mockResponseDto;
    private SuccessResponse<UpdateCosmeticResponse> successResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "Admin", true);
        mockCosmeticId = UUID.randomUUID().toString();
        mockRequest = mock(UpdateCosmeticRequest.class);
        mockInput = mock(UpdateCosmeticInput.class);
        mockOutput = mock(UpdateCosmeticOutput.class);
        mockResponseDto = mock(UpdateCosmeticResponse.class);
        successResponse = new SuccessResponse<>(true, mockResponseDto);
    }

    @Test
    @DisplayName("Deve atualizar o cosmético com sucesso retornando status 200 OK e o payload estruturado")
    void handle_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<UpdateCosmeticMapper> mapperMock = mockStatic(UpdateCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> UpdateCosmeticMapper.toInput(mockAuthId, mockRequest, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> UpdateCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.ok(successResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> response = controller.handle(principal, mockRequest, mockCosmeticId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original sem tratamento local quando a execução do UseCase falhar")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<UpdateCosmeticMapper> mapperMock = mockStatic(UpdateCosmeticMapper.class)) {

            mapperMock.when(() -> UpdateCosmeticMapper.toInput(mockAuthId, mockRequest, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Cosmetic conflict, invalid storage or missing permissions"));

            assertThrows(RuntimeException.class, () -> controller.handle(principal, mockRequest, mockCosmeticId));

            mapperMock.verify(() -> UpdateCosmeticMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento estrutural resiliente caso o Mapper de resposta retorne nulo (Comportamento Desejado/Ausente)")
    void handle_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<UpdateCosmeticMapper> mapperMock = mockStatic(UpdateCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> UpdateCosmeticMapper.toInput(mockAuthId, mockRequest, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> UpdateCosmeticMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> response = controller.handle(principal, mockRequest, mockCosmeticId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}