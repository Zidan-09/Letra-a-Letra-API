package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.CreateItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateItemControllerTest {

    @Mock
    private UseCase<CreateItemInput, CreateItemOutput> useCase;

    @InjectMocks
    private CreateItemController controller;

    private AuthenticatedUser principal;
    private CreateItemInput mockInput;
    private CreateItemOutput mockOutput;
    private CreateItemRequest request;
    private ItemResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<ItemResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        mockInput = mock(CreateItemInput.class);
        mockOutput = mock(CreateItemOutput.class);
        request = mock(CreateItemRequest.class);
        mockResponseDto = mock(ItemResponse.class);

        SuccessResponse<ItemResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should create definition and return success wrapper response")
    void shouldCreateDefinition() {
        try (MockedStatic<CreateItemMapper> mapperMock = mockStatic(CreateItemMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> CreateItemMapper.toInput(principal, request, null))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> CreateItemMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<ItemResponse>> response =
                    controller.handle(principal, request, null);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
