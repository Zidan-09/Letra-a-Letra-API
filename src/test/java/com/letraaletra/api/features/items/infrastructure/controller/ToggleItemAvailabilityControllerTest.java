package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.ToggleItemAvailabilityRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.ToggleItemAvailabilityMapper;
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
class ToggleItemAvailabilityControllerTest {

    @Mock
    private UseCase<ToggleItemAvailabilityInput, ToggleItemAvailabilityOutput> useCase;

    @InjectMocks
    private ToggleItemAvailabilityController controller;

    private AuthenticatedUser principal;
    private UUID itemId;
    private ToggleItemAvailabilityInput mockInput;
    private ToggleItemAvailabilityOutput mockOutput;
    private ToggleItemAvailabilityRequest request;
    private ItemResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<ItemResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        itemId = UUID.randomUUID();
        mockInput = mock(ToggleItemAvailabilityInput.class);
        mockOutput = mock(ToggleItemAvailabilityOutput.class);
        request = mock(ToggleItemAvailabilityRequest.class);
        mockResponseDto = mock(ItemResponse.class);

        SuccessResponse<ItemResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should toggle availability and return success wrapper response")
    void shouldToggleAvailability() {
        try (MockedStatic<ToggleItemAvailabilityMapper> mapperMock = mockStatic(ToggleItemAvailabilityMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> ToggleItemAvailabilityMapper.toInput(principal, itemId, request))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> ToggleItemAvailabilityMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<ItemResponse>> response =
                    controller.handle(principal, itemId, request);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
