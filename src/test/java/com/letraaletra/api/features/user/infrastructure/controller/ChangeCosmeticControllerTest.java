package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
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
class ChangeCosmeticControllerTest {

    @Mock
    private UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> changeCosmeticUseCase;

    @InjectMocks
    private ChangeCosmeticController controller;

    private UUID authUserId;
    private AuthenticatedUser principal;
    private UUID cosmeticId;
    private ChangeCosmeticInput mockInput;
    private ChangeCosmeticOutput mockOutput;
    private ChangeCosmeticResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<ChangeCosmeticResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        principal = new AuthenticatedUser(authUserId, "User", false);
        cosmeticId = UUID.randomUUID();
        mockInput = mock(ChangeCosmeticInput.class);
        mockOutput = mock(ChangeCosmeticOutput.class);
        mockResponseDto = mock(ChangeCosmeticResponse.class);

        SuccessResponse<ChangeCosmeticResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully process request, execute use case and return success response wrapper")
    void shouldSuccessfullyChangeCosmetic() {
        try (MockedStatic<ChangeCosmeticMapper> mapperMock = mockStatic(ChangeCosmeticMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> ChangeCosmeticMapper.toInput(cosmeticId, authUserId)).thenReturn(mockInput);
            when(changeCosmeticUseCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> ChangeCosmeticMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<ChangeCosmeticResponse>> response = controller.handle(principal, cosmeticId);

            assertEquals(mockResponseEntity, response);
            verify(changeCosmeticUseCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate runtime exceptions thrown by the use case layer directly to the controller advisor")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<ChangeCosmeticMapper> mapperMock = mockStatic(ChangeCosmeticMapper.class)) {

            mapperMock.when(() -> ChangeCosmeticMapper.toInput(cosmeticId, authUserId)).thenReturn(mockInput);
            when(changeCosmeticUseCase.execute(mockInput)).thenThrow(new RuntimeException("Business rule violation"));

            assertThrows(RuntimeException.class, () -> controller.handle(principal, cosmeticId));
        }
    }

    @Test
    @DisplayName("Should handle situation where mapper mapping throws a conversion or parsing error")
    void shouldPropagateMapperExceptions() {
        try (MockedStatic<ChangeCosmeticMapper> mapperMock = mockStatic(ChangeCosmeticMapper.class)) {

            mapperMock.when(() -> ChangeCosmeticMapper.toInput(cosmeticId, authUserId))
                    .thenThrow(new IllegalArgumentException("Invalid UUID parameters mapping"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal, cosmeticId));
            verifyNoInteractions(changeCosmeticUseCase);
        }
    }
}