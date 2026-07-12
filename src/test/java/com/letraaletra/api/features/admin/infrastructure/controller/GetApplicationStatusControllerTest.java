package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetApplicationStatusInput;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetApplicationStatusResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetApplicationStatusMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetApplicationStatusControllerTest {

    @Mock
    private UseCase<GetApplicationStatusInput, GetApplicationStatusOutput> useCase;

    @Mock
    private AuthenticatedUser principal;

    @Mock
    private GetApplicationStatusOutput output;

    @Mock
    private GetApplicationStatusResponse responseDto;

    @Mock
    private SuccessResponse<GetApplicationStatusResponse> successResponse;

    private GetApplicationStatusController controller;

    @BeforeEach
    void setUp() {
        controller = new GetApplicationStatusController(useCase);
    }

    @Test
    @DisplayName("Should return 200 OK with application status when execution is successful")
    void shouldReturnApplicationStatusSuccessfully() {
        try (MockedStatic<GetApplicationStatusMapper> mapperMock = mockStatic(GetApplicationStatusMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            UUID mockAuth = UUID.randomUUID();
            GetApplicationStatusInput mockInput = new GetApplicationStatusInput(mockAuth);
            ResponseEntity<SuccessResponse<GetApplicationStatusResponse>> expectedResponseEntity =
                    new ResponseEntity<>(successResponse, HttpStatus.OK);

            when(principal.auth()).thenReturn(mockAuth);

            mapperMock.when(() -> GetApplicationStatusMapper.toInput(mockAuth))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenReturn(output);

            mapperMock.when(() -> GetApplicationStatusMapper.toResponse(output))
                    .thenReturn(responseDto);

            apiResponseMock.when(() -> ApiResponseService.success(responseDto))
                    .thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetApplicationStatusResponse>> result = controller.handle(principal);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(successResponse, result.getBody());

            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate exception when UseCase execution fails")
    void shouldPropagateExceptionWhenUseCaseFails() {
        try (MockedStatic<GetApplicationStatusMapper> mapperMock = mockStatic(GetApplicationStatusMapper.class)) {

            UUID mockAuth = UUID.randomUUID();
            GetApplicationStatusInput mockInput = new GetApplicationStatusInput(mockAuth);

            when(principal.auth()).thenReturn(mockAuth);

            mapperMock.when(() -> GetApplicationStatusMapper.toInput(mockAuth))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenThrow(new SecurityException("Access Denied"));

            SecurityException exception = assertThrows(SecurityException.class, () ->
                    controller.handle(principal)
            );

            assertEquals("Access Denied", exception.getMessage());
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should handle case where principal returns a null auth payload")
    void shouldHandleNullAuthFromPrincipal() {
        try (MockedStatic<GetApplicationStatusMapper> mapperMock = mockStatic(GetApplicationStatusMapper.class)) {
            when(principal.auth()).thenReturn(null);

            mapperMock.when(() -> GetApplicationStatusMapper.toInput(null))
                    .thenThrow(new IllegalArgumentException("Auth credentials cannot be null"));

            assertThrows(IllegalArgumentException.class, () -> {
                controller.handle(principal);
            }, "Mapper or Controller should validate against null credentials inside principal");
        }
    }
}