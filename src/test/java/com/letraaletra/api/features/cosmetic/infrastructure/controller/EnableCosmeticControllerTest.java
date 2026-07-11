package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.EnableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.EnableCosmeticMapper;
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
class EnableCosmeticControllerTest {

    @Mock
    private UseCase<EnableCosmeticInput, EnableCosmeticOutput> useCase;

    @InjectMocks
    private EnableCosmeticController controller;

    private UUID mockAuthId;
    private AuthenticatedUser principal;
    private String mockCosmeticId;
    private EnableCosmeticInput mockInput;
    private EnableCosmeticOutput mockOutput;
    private EnableCosmeticResponse mockResponseDto;
    private SuccessResponse<EnableCosmeticResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(mockAuthId, "Admin", true);
        mockCosmeticId = UUID.randomUUID().toString();

        mockInput = mock(EnableCosmeticInput.class);
        mockOutput = mock(EnableCosmeticOutput.class);
        mockResponseDto = mock(EnableCosmeticResponse.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve habilitar o cosmético com sucesso retornando 200 OK e a resposta envelopada")
    void handle_ShouldReturnSuccessResponse_WhenValidParametersAreProvided() {
        try (MockedStatic<EnableCosmeticMapper> mapperMock = mockStatic(EnableCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> EnableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> EnableCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<EnableCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<EnableCosmeticResponse>> response = controller.handle(principal, mockCosmeticId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original e não mascarar erros quando o UseCase falhar")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<EnableCosmeticMapper> mapperMock = mockStatic(EnableCosmeticMapper.class)) {

            mapperMock.when(() -> EnableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Cosmetic already enabled or business violation"));

            assertThrows(RuntimeException.class, () -> controller.handle(principal, mockCosmeticId));

            mapperMock.verify(() -> EnableCosmeticMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve garantir resiliência se a conversão do DTO de resposta retornar nula (Comportamento Desejado/Ausente)")
    void handle_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<EnableCosmeticMapper> mapperMock = mockStatic(EnableCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> EnableCosmeticMapper.toInput(mockAuthId, mockCosmeticId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> EnableCosmeticMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<EnableCosmeticResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<EnableCosmeticResponse>> response = controller.handle(principal, mockCosmeticId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}