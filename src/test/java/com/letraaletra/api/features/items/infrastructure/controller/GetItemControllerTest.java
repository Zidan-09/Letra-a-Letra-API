package com.letraaletra.api.features.items.infrastructure.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.GetItemMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;

@ExtendWith(MockitoExtension.class)
class GetItemControllerTest {

    @Mock
    private UseCase<GetItemInput, GetItemOutput> useCase;

    @InjectMocks
    private GetItemController controller;

    @Mock
    private AuthenticatedUser principal;

    @Mock
    private GetItemInput mockInput;

    @Mock
    private GetItemOutput mockOutput;

    @Mock
    private ItemResponse mockResponseDto;

    @SuppressWarnings("rawtypes")
    @Mock
    private ResponseEntity mockResponseEntity;

    @Test
    @SuppressWarnings("unchecked")
    void handleShouldExecuteUseCaseAndReturnSuccess() {
        UUID itemId = UUID.randomUUID();
        try (MockedStatic<GetItemMapper> mapperMock = mockStatic(GetItemMapper.class);
                MockedStatic<ApiResponseHandler> handlerMock = mockStatic(ApiResponseHandler.class)) {
            when(GetItemMapper.toInput(principal, itemId)).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            when(GetItemMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            when(ApiResponseHandler.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity result = controller.handle(principal, itemId);

            assertSame(mockResponseEntity, result);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
