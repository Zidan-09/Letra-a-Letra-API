package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.GoogleAuthRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.AuthUserMapper;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GoogleAuthMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthControllerTest {

    @Mock
    private UseCase<AuthInput, SignInOutput> googleAuthUseCase;

    @InjectMocks
    private GoogleAuthController controller;

    private GoogleAuthRequest mockRequest;
    private AuthInput mockInput;
    private SignInOutput mockOutput;
    private AuthUserResponse mockResponseDto;
    private ResponseEntity<SuccessResponse<AuthUserResponse>> mockResponseEntity;

    @BeforeEach
    void setUp() {
        mockRequest = mock(GoogleAuthRequest.class);
        mockInput = mock(AuthInput.class);
        mockOutput = mock(SignInOutput.class);
        mockResponseDto = mock(AuthUserResponse.class);

        SuccessResponse<AuthUserResponse> successResponse = new SuccessResponse<>(true, mockResponseDto);
        mockResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should successfully authenticate with Google, execute use case and return success response wrapper")
    void shouldAuthenticateWithGoogleSuccessfully() {
        try (MockedStatic<GoogleAuthMapper> googleMapperMock = mockStatic(GoogleAuthMapper.class);
             MockedStatic<AuthUserMapper> authMapperMock = mockStatic(AuthUserMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            googleMapperMock.when(() -> GoogleAuthMapper.toInput(mockRequest)).thenReturn(mockInput);
            when(googleAuthUseCase.execute(mockInput)).thenReturn(mockOutput);
            authMapperMock.when(() -> AuthUserMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(mockResponseEntity);

            ResponseEntity<SuccessResponse<AuthUserResponse>> response = controller.handle(mockRequest);

            assertEquals(mockResponseEntity, response);
            verify(googleAuthUseCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate infrastructure/business exceptions thrown during authentication use case execution")
    void shouldPropagateUseCaseExceptions() {
        try (MockedStatic<GoogleAuthMapper> googleMapperMock = mockStatic(GoogleAuthMapper.class)) {

            googleMapperMock.when(() -> GoogleAuthMapper.toInput(mockRequest)).thenReturn(mockInput);
            when(googleAuthUseCase.execute(mockInput)).thenThrow(new RuntimeException("Invalid Google ID Token or platform error"));

            assertThrows(RuntimeException.class, () -> controller.handle(mockRequest));
        }
    }

    @Test
    @DisplayName("Should handle edge case gracefully when input body parameters or mapping context resolves to null")
    void shouldThrowExceptionWhenRequestBodyMappingProducesNullInput() {
        try (MockedStatic<GoogleAuthMapper> googleMapperMock = mockStatic(GoogleAuthMapper.class)) {

            googleMapperMock.when(() -> GoogleAuthMapper.toInput(null)).thenReturn(null);
            when(googleAuthUseCase.execute(null)).thenThrow(new IllegalArgumentException("Auth input cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(null));
        }
    }

    @Test
    @DisplayName("Should interrupt execution flow when Google request body parsing conversion mapping fails")
    void shouldPropagateGoogleMapperExceptions() {
        try (MockedStatic<GoogleAuthMapper> googleMapperMock = mockStatic(GoogleAuthMapper.class)) {

            googleMapperMock.when(() -> GoogleAuthMapper.toInput(mockRequest))
                    .thenThrow(new IllegalArgumentException("Failed to decode token payloads internally"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(mockRequest));
            verifyNoInteractions(googleAuthUseCase);
        }
    }

    @Test
    @DisplayName("Should interrupt flow if response mapper mapping layer execution fails unexpectedly")
    void shouldPropagateAuthUserMapperExceptions() {
        try (MockedStatic<GoogleAuthMapper> googleMapperMock = mockStatic(GoogleAuthMapper.class);
             MockedStatic<AuthUserMapper> authMapperMock = mockStatic(AuthUserMapper.class)) {

            googleMapperMock.when(() -> GoogleAuthMapper.toInput(mockRequest)).thenReturn(mockInput);
            when(googleAuthUseCase.execute(mockInput)).thenReturn(mockOutput);
            authMapperMock.when(() -> AuthUserMapper.toResponse(mockOutput))
                    .thenThrow(new IllegalStateException("Missing expected identity profile claims during presentation mapping"));

            assertThrows(IllegalStateException.class, () -> controller.handle(mockRequest));
        }
    }
}