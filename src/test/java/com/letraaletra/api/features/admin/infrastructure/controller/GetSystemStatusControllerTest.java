package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetSystemStatusInput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetSystemStatusResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetSystemStatusMapper;
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
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSystemStatusControllerTest {

    @Mock
    private UseCase<GetSystemStatusInput, GetSystemStatusOutput> useCase;

    @Mock
    private AuthenticatedUser principal;

    @Mock
    private GetSystemStatusOutput output;

    @Mock
    private GetSystemStatusResponse responseDto;

    @Mock
    private SuccessResponse<GetSystemStatusResponse> successResponse;

    private GetSystemStatusController controller;

    @BeforeEach
    void setUp() {
        controller = new GetSystemStatusController(useCase);
    }

    @Test
    @DisplayName("Should return 200 OK with system status when execution is successful")
    void shouldReturnSystemStatusSuccessfully() {
        try (MockedStatic<GetSystemStatusMapper> mapperMock = mockStatic(GetSystemStatusMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            UUID mockAuth = UUID.randomUUID();
            GetSystemStatusInput mockInput = new GetSystemStatusInput(mockAuth);
            ResponseEntity<SuccessResponse<GetSystemStatusResponse>> expectedResponseEntity =
                    new ResponseEntity<>(successResponse, HttpStatus.OK);

            when(principal.auth()).thenReturn(mockAuth);

            mapperMock.when(() -> GetSystemStatusMapper.toInput(mockAuth))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenReturn(output);

            mapperMock.when(() -> GetSystemStatusMapper.toResponse(output))
                    .thenReturn(responseDto);

            apiResponseMock.when(() -> ApiResponseService.success(responseDto))
                    .thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetSystemStatusResponse>> result = controller.handle(principal);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(successResponse, result.getBody());

            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should propagate exception when UseCase verification or execution throws error")
    void shouldPropagateExceptionWhenUseCaseFails() {
        try (MockedStatic<GetSystemStatusMapper> mapperMock = mockStatic(GetSystemStatusMapper.class)) {

            UUID mockAuth = UUID.randomUUID();
            GetSystemStatusInput mockInput = new GetSystemStatusInput(mockAuth);

            when(principal.auth()).thenReturn(mockAuth);

            mapperMock.when(() -> GetSystemStatusMapper.toInput(mockAuth))
                    .thenReturn(mockInput);

            when(useCase.execute(mockInput))
                    .thenThrow(new SecurityException("Access denied: Insufficient privileges"));

            SecurityException exception = assertThrows(SecurityException.class, () ->
                    controller.handle(principal)
            );

            assertEquals("Access denied: Insufficient privileges", exception.getMessage());
            verify(useCase, times(1)).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Should verify contract route definition maps precisely to /system using HTTP GET")
    void shouldMaintainCorrectRouteAndMappingContract() throws NoSuchMethodException {
        var method = GetSystemStatusController.class.getMethod("handle", AuthenticatedUser.class);

        assertTrue(method.isAnnotationPresent(GetMapping.class), "The endpoint handler must use @GetMapping");

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        assertArrayEquals(new String[]{"/system"}, getMapping.path(), "Endpoint routing contract altered unexpectedly");
    }

    @Test
    @DisplayName("Should handle scenarios where principal returns blank or null auth tokens gracefully")
    void shouldValidateAgainstNullAuthInsidePrincipal() {
        try (MockedStatic<GetSystemStatusMapper> mapperMock = mockStatic(GetSystemStatusMapper.class)) {
            when(principal.auth()).thenReturn(null);

            mapperMock.when(() -> GetSystemStatusMapper.toInput(null))
                    .thenThrow(new IllegalArgumentException("Authentication payload cannot be extracted"));

            assertThrows(IllegalArgumentException.class, () ->
                    controller.handle(principal), "System should fail immediately when mapped token dependencies resolution evaluates into a null input");
        }
    }
}