package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.GetCosmeticsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCosmeticsControllerTest {

    @Mock
    private UseCase<GetCosmeticsInput, GetCosmeticsOutput> useCase;

    @InjectMocks
    private GetCosmeticsController controller;

    private Pageable pageable;
    private GetCosmeticsInput input;
    private GetCosmeticsOutput output;
    private PageResponse responseDto;
    private SuccessResponse successResponse;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);

        input = mock(GetCosmeticsInput.class);
        output = mock(GetCosmeticsOutput.class);
        responseDto = mock(PageResponse.class);

        successResponse = new SuccessResponse<PageResponse>(
                true,
                responseDto
        );
    }

    @Test
    @DisplayName("Deve retornar a lista paginada de cosméticos com sucesso")
    void handle_ShouldReturnCosmeticsList_WhenPageableIsValid() {

        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(pageable))
                    .thenReturn(input);

            when(useCase.execute(input))
                    .thenReturn(output);

            mapperMock.when(() -> GetCosmeticsMapper.toResponse(output))
                    .thenReturn(responseDto);

            ResponseEntity<SuccessResponse> expected =
                    ResponseEntity.ok(successResponse);

            apiResponseMock.when(() -> ApiResponseService.success(responseDto))
                    .thenReturn(expected);

            ResponseEntity<SuccessResponse<PageResponse<CosmeticDTO>>> response =
                    controller.handle(pageable);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(input);
        }
    }

    @Test
    @DisplayName("Deve propagar erro do UseCase")
    void handle_ShouldPropagateException_WhenUseCaseThrowsException() {

        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(pageable))
                    .thenReturn(input);

            when(useCase.execute(input))
                    .thenThrow(new RuntimeException("Database timeout error"));

            assertThrows(
                    RuntimeException.class,
                    () -> controller.handle(pageable)
            );

            mapperMock.verify(() -> GetCosmeticsMapper.toResponse(any()), never());

            verify(useCase).execute(input);
        }
    }

    @Test
    @DisplayName("Deve propagar erro se o mapper de entrada falhar")
    void handle_ShouldThrowException_WhenMapperToInputFails() {

        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(null))
                    .thenThrow(new NullPointerException("Pageable cannot be null"));

            assertThrows(
                    NullPointerException.class,
                    () -> controller.handle(null)
            );

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve retornar sucesso quando não houver cosméticos")
    void handle_ShouldReturnSuccessWithEmptyData_WhenNoCosmeticsAreFound() {

        try (MockedStatic<GetCosmeticsMapper> mapperMock = mockStatic(GetCosmeticsMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetCosmeticsMapper.toInput(pageable))
                    .thenReturn(input);

            when(useCase.execute(input))
                    .thenReturn(output);

            mapperMock.when(() -> GetCosmeticsMapper.toResponse(output))
                    .thenReturn(responseDto);

            ResponseEntity<SuccessResponse<PageResponse<CosmeticDTO>>> expected =
                    ResponseEntity.ok(successResponse);

            apiResponseMock.when(() -> ApiResponseService.success(responseDto))
                    .thenReturn(expected);

            ResponseEntity<SuccessResponse<PageResponse<CosmeticDTO>>> response =
                    controller.handle(pageable);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(successResponse, response.getBody());

            verify(useCase).execute(input);
        }
    }
}