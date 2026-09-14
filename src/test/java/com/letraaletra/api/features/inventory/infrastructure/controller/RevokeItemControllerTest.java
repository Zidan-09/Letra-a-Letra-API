package com.letraaletra.api.features.inventory.infrastructure.controller;

import com.letraaletra.api.features.inventory.application.input.RevokeItemInput;
import com.letraaletra.api.features.inventory.application.output.RevokeItemOutput;
import com.letraaletra.api.features.inventory.infrastructure.presentation.mapper.RevokeItemMapper;
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
class RevokeItemControllerTest {

    @Mock
    private UseCase<RevokeItemInput, RevokeItemOutput> useCase;

    @InjectMocks
    private RevokeItemController controller;

    private AuthenticatedUser principal;
    private UUID itemId;
    private RevokeItemInput mockInput;
    private ResponseEntity<SuccessResponse<Void>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        itemId = UUID.randomUUID();
        mockInput = mock(RevokeItemInput.class);

        SuccessResponse<Void> successResponse = new SuccessResponse<>(true, null);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should revoke item and return no content")
    void shouldRevokeItem() {
        try (MockedStatic<RevokeItemMapper> mapperMock = mockStatic(RevokeItemMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> RevokeItemMapper.toInput(principal, itemId))
                    .thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mock(RevokeItemOutput.class));
            apiResponseMock.when(() -> ApiResponseHandler.success(null, HttpStatus.NO_CONTENT))
                    .thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<Void>> response = controller.handle(principal, itemId);

            assertEquals(mockResponseEntity, response);
            verify(useCase, times(1)).execute(mockInput);
        }
    }
}
