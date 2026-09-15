package com.letraaletra.api.features.items.infrastructure.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.features.items.infrastructure.presentation.mapper.ListItemDefinitionsMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

@ExtendWith(MockitoExtension.class)
class ListItemDefinitionsControllerTest {

    @Mock
    private UseCase<ListItemDefinitionsInput, ListItemDefinitionsOutput> useCase;

    @InjectMocks
    private ListItemDefinitionsController controller;

    @Mock
    private AuthenticatedUser principal;

    @Mock
    private ListItemDefinitionsInput mockInput;

    @Mock
    private ListItemDefinitionsOutput mockOutput;

    @Mock
    private PageResponse<ItemDefinitionResponse> mockPageDto;

    @SuppressWarnings("rawtypes")
    @Mock
    private ResponseEntity mockResponseEntity;

    @Test
    @SuppressWarnings("unchecked")
    void handleShouldExecuteUseCaseAndReturnSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        try (MockedStatic<ListItemDefinitionsMapper> mapperMock = mockStatic(ListItemDefinitionsMapper.class);
                MockedStatic<ApiResponseHandler> handlerMock = mockStatic(ApiResponseHandler.class)) {
            when(ListItemDefinitionsMapper.toInput(principal, "COSMETIC", null, null, pageable))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            when(ListItemDefinitionsMapper.toResponse(mockOutput)).thenReturn(mockPageDto);
            when(ApiResponseHandler.success(mockPageDto)).thenReturn(mockResponseEntity);

            ResponseEntity result = controller.handle(principal, "COSMETIC", null, null, pageable);

            assertSame(mockResponseEntity, result);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
