package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.UpdateItemDefinitionRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.UpdateItemDefinitionMapper;
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
class UpdateItemDefinitionControllerTest {

    @Mock
    private UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> useCase;

    @InjectMocks
    private UpdateItemDefinitionController controller;

    private AuthenticatedUser principal;
    private UUID itemId;
    private UpdateItemDefinitionInput mockInput;
    private UpdateItemDefinitionOutput mockOutput;
    private UpdateItemDefinitionRequest request;
    private ItemDefinitionResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<ItemDefinitionResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        itemId = UUID.randomUUID();
        mockInput = mock(UpdateItemDefinitionInput.class);
        mockOutput = mock(UpdateItemDefinitionOutput.class);
        request = mock(UpdateItemDefinitionRequest.class);
        mockResponseDto = mock(ItemDefinitionResponse.class);

        SuccessResponse<ItemDefinitionResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should update definition and return success wrapper response")
    void shouldUpdateDefinition() {
        try (MockedStatic<UpdateItemDefinitionMapper> mapperMock = mockStatic(UpdateItemDefinitionMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> UpdateItemDefinitionMapper.toInput(principal, itemId, request, null))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> UpdateItemDefinitionMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<ItemDefinitionResponse>> response =
                    controller.handle(principal, itemId, request, null);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
