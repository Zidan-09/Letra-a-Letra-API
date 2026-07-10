package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.RegisterAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.RegisterAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.RegisterAdminMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: RegisterAdminController")
class RegisterAdminControllerTest {

    @Mock
    private UseCase<RegisterAdminInput, RegisterAdminOutput> useCase;

    @InjectMocks
    private RegisterAdminController registerAdminController;

    private MockedStatic<RegisterAdminMapper> registerAdminMapperMockedStatic;
    private MockedStatic<ApiResponseService> apiResponseServiceMockedStatic;

    private UUID requesterId;
    private RegisterAdminRequest request;
    private RegisterAdminInput input;
    private RegisterAdminOutput output;
    private RegisterAdminResponse responseDto;
    private ResponseEntity<SuccessResponse<RegisterAdminResponse>> expectedResponseEntity;

    @BeforeEach
    void setUp() {
        registerAdminMapperMockedStatic = mockStatic(RegisterAdminMapper.class);
        apiResponseServiceMockedStatic = mockStatic(ApiResponseService.class);

        requesterId = UUID.randomUUID();
        request = new RegisterAdminRequest("Novo Admin", "novo.admin@letraaletra.com", "SecretPassword2026!");
        input = new RegisterAdminInput(requesterId, "Novo Admin", "novo.admin@letraaletra.com", "SecretPassword2026!");

        Admin adminMock = mock(Admin.class);
        output = new RegisterAdminOutput(adminMock);
        responseDto = new RegisterAdminResponse("Novo Admin", "novo.admin@letraaletra.com");

        SuccessResponse<RegisterAdminResponse> successResponse = new SuccessResponse<>(true, responseDto);
        expectedResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @AfterEach
    void tearDown() {
        registerAdminMapperMockedStatic.close();
        apiResponseServiceMockedStatic.close();
    }

    @Test
    @DisplayName("Should successfully register admin and return 200 OK when parameters are valid")
    void shouldRegisterAdminSuccessfully() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(requesterId, request)).thenReturn(input);
        when(useCase.execute(input)).thenReturn(output);
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toResponse(output)).thenReturn(responseDto);
        apiResponseServiceMockedStatic.when(() -> ApiResponseService.success(responseDto)).thenReturn(expectedResponseEntity);

        ResponseEntity<SuccessResponse<RegisterAdminResponse>> result = registerAdminController.handle(requesterId, request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponseEntity, result);

        verify(useCase, times(1)).execute(input);
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toInput(requesterId, request), times(1));
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toResponse(output), times(1));
        apiResponseServiceMockedStatic.verify(() -> ApiResponseService.success(responseDto), times(1));
    }

    @Test
    @DisplayName("Should propagate EmailAlreadyInUseException when domain usecase detects conflicting record email")
    void shouldPropagateEmailAlreadyInUseExceptionFromUseCase() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(requesterId, request)).thenReturn(input);
        when(useCase.execute(input)).thenThrow(new EmailAlreadyInUseException());

        assertThrows(EmailAlreadyInUseException.class, () -> registerAdminController.handle(requesterId, request));

        verify(useCase, times(1)).execute(input);
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toInput(requesterId, request), times(1));
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toResponse(any()), never());
        apiResponseServiceMockedStatic.verify(() -> ApiResponseService.success(any()), never());
    }

    @Test
    @DisplayName("Should propagate SecurityException when requester principal authentication context fails domain rules")
    void shouldPropagateSecurityExceptionFromUseCase() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(requesterId, request)).thenReturn(input);
        when(useCase.execute(input)).thenThrow(new SecurityException("Requester permissions are insufficient"));

        assertThrows(SecurityException.class, () -> registerAdminController.handle(requesterId, request));

        verify(useCase, times(1)).execute(input);
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toInput(requesterId, request), times(1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when authentication principal UUID is missing (Expected Missing Behavior)")
    void shouldThrowExceptionWhenAuthenticationPrincipalIsNull() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(null, request))
                .thenThrow(new IllegalArgumentException("Authenticated user identity context cannot be missing"));

        assertThrows(IllegalArgumentException.class, () -> registerAdminController.handle(null, request));

        verifyNoInteractions(useCase);
    }

    @Test
    @DisplayName("Should throw NullPointerException when client web payload body content is null (Expected Missing Behavior)")
    void shouldThrowExceptionWhenRequestBodyPayloadIsNull() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(requesterId, null))
                .thenThrow(new NullPointerException("Request structure mapping target references null body"));

        assertThrows(NullPointerException.class, () -> registerAdminController.handle(requesterId, null));

        verifyNoInteractions(useCase);
    }

    @Test
    @DisplayName("Should propagate application runtime exceptions occurred inside structured static mapper components")
    void shouldPropagateRuntimeExceptionsFromMapper() {
        registerAdminMapperMockedStatic.when(() -> RegisterAdminMapper.toInput(requesterId, request))
                .thenThrow(new IllegalArgumentException("Structural field data extraction error"));

        assertThrows(IllegalArgumentException.class, () -> registerAdminController.handle(requesterId, request));

        verifyNoInteractions(useCase);
        registerAdminMapperMockedStatic.verify(() -> RegisterAdminMapper.toInput(requesterId, request), times(1));
    }
}