package com.letraaletra.api.features.offers.infrastructure.controller;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.infrastructure.presentation.mapper.GetOffersMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOffersControllerTest {

    @Mock
    private UseCase<GetOffersInput, GetOffersOutput> useCase;

    @InjectMocks
    private GetOffersController controller;

    private Pageable mockPageable;
    private GetOffersInput mockInput;
    private GetOffersOutput mockOutput;
    private PageResponse<Offer> mockResponseDto;
    private ResponseEntity<SuccessResponse<PageResponse<Offer>>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        mockPageable = mock(Pageable.class);
        mockInput = mock(GetOffersInput.class);
        mockOutput = mock(GetOffersOutput.class);

        mockResponseDto = mock(PageResponse.class);

        SuccessResponse<PageResponse<Offer>> successResponse =
                new SuccessResponse<>(true, mockResponseDto);

        mockResponseEntity =
                new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully handle get offers request")
    void shouldGetOffersSuccessfully() {

        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenReturn(mockOutput);

            mapperMock.when(() -> GetOffersMapper.toResponse(mockOutput))
                    .thenReturn(mockResponseDto);

            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto))
                    .thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<PageResponse<Offer>>> response =
                    controller.handle(mockPageable);

            assertEquals(mockResponseEntity, response);

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate use case exceptions")
    void shouldPropagateUseCaseExceptions() {

        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenThrow(new RuntimeException("Database failure"));

            assertThrows(
                    RuntimeException.class,
                    () -> controller.handle(mockPageable)
            );
        }
    }

    @Test
    @DisplayName("Should propagate mapper input exceptions")
    void shouldPropagateInputMapperExceptions() {

        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable))
                    .thenThrow(new IllegalArgumentException("Mapper error"));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> controller.handle(mockPageable)
            );

            verifyNoInteractions(useCase);
        }
    }

    @Test
    @DisplayName("Should propagate mapper response exceptions")
    void shouldPropagateResponseMapperExceptions() {

        try (MockedStatic<GetOffersMapper> mapperMock = mockStatic(GetOffersMapper.class)) {

            mapperMock.when(() -> GetOffersMapper.toInput(mockPageable))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenReturn(mockOutput);

            mapperMock.when(() -> GetOffersMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Mapper response error"));

            assertThrows(
                    IllegalStateException.class,
                    () -> controller.handle(mockPageable)
            );
        }
    }
}