package com.letraaletra.api.features.items.infrastructure.controller;

import com.letraaletra.api.features.items.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemDefinitionRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.CreateItemDefinitionMapper;
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
class CreateItemDefinitionControllerTest {

    @Mock
    private UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> useCase;

    @InjectMocks
    private CreateItemDefinitionController controller;

    private AuthenticatedUser principal;
    private CreateItemDefinitionInput mockInput;
    private CreateItemDefinitionOutput mockOutput;
    private CreateItemDefinitionRequest request;
    private ItemDefinitionResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<ItemDefinitionResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        mockInput = mock(CreateItemDefinitionInput.class);
        mockOutput = mock(CreateItemDefinitionOutput.class);
        request = mock(CreateItemDefinitionRequest.class);
        mockResponseDto = mock(ItemDefinitionResponse.class);

        SuccessResponse<ItemDefinitionResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should create definition and return success wrapper response")
    void shouldCreateDefinition() {
        try (MockedStatic<CreateItemDefinitionMapper> mapperMock = mockStatic(CreateItemDefinitionMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> CreateItemDefinitionMapper.toInput(principal, request, null))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> CreateItemDefinitionMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<ItemDefinitionResponse>> response =
                    controller.handle(principal, request, null);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
