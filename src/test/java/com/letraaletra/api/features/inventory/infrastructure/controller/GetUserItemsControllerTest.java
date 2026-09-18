package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.GetUserItemsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.GetUserItemsMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserItemsControllerTest {

    @Mock
    private UseCase<GetUserItemsInput, GetUserItemsOutput> useCase;

    @InjectMocks
    private GetUserItemsController controller;

    private UUID authUserId;
    private AuthenticatedUser principal;
    private GetUserItemsInput mockInput;
    private GetUserItemsOutput mockOutput;
    private GetUserItemsResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<GetUserItemsResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        principal = new AuthenticatedUser(authUserId, "User", false, false);
        mockInput = mock(GetUserItemsInput.class);
        mockOutput = mock(GetUserItemsOutput.class);
        mockResponseDto = mock(GetUserItemsResponse.class);

        SuccessResponse<GetUserItemsResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should list items with filters and return success wrapper response")
    void shouldListItemsWithFilters() {
        try (MockedStatic<GetUserItemsMapper> mapperMock = mockStatic(GetUserItemsMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetUserItemsMapper.toInput(authUserId, "EQUIPPABLE", null, null, null))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetUserItemsMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<GetUserItemsResponse>> response =
                    controller.handle(principal, "EQUIPPABLE", null, null, null);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate use case exceptions")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GetUserItemsMapper> mapperMock = mockStatic(GetUserItemsMapper.class)) {

            mapperMock.when(() -> GetUserItemsMapper.toInput(authUserId, null, null, null, null))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("timeout"));

            assertThrows(RuntimeException.class,
                    () -> controller.handle(principal, null, null, null, null));
        }
    }
}
