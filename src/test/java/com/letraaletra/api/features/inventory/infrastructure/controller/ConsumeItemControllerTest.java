package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.ConsumeItemRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.InventoryMovementsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.ConsumeItemMapper;
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
class ConsumeItemControllerTest {

    @Mock
    private UseCase<ConsumeItemInput, ConsumeItemOutput> useCase;

    @InjectMocks
    private ConsumeItemController controller;

    private UUID authUserId;
    private UUID itemId;
    private AuthenticatedUser principal;
    private ConsumeItemInput mockInput;
    private ConsumeItemOutput mockOutput;
    private ConsumeItemRequest request;
    private InventoryMovementsResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<InventoryMovementsResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        principal = new AuthenticatedUser(authUserId, "User", false, false);
        mockInput = mock(ConsumeItemInput.class);
        mockOutput = mock(ConsumeItemOutput.class);
        request = mock(ConsumeItemRequest.class);
        mockResponseDto = mock(InventoryMovementsResponse.class);

        SuccessResponse<InventoryMovementsResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should consume item and return movements wrapper response")
    void shouldConsumeItem() {
        try (MockedStatic<ConsumeItemMapper> mapperMock = mockStatic(ConsumeItemMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> ConsumeItemMapper.toInput(authUserId, itemId, request))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> ConsumeItemMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<InventoryMovementsResponse>> response =
                    controller.handle(principal, itemId, request);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
