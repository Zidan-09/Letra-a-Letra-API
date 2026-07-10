package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.AuthAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.AuthAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.AuthAdminMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
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
@DisplayName("Unit Tests: AuthAdminController")
class AuthAdminControllerTest {

    @Mock
    private UseCase<AuthAdminInput, AuthAdminOutput> useCase;

    @InjectMocks
    private AuthAdminController authAdminController;

    private MockedStatic<AuthAdminMapper> authAdminMapperMockedStatic;
    private MockedStatic<ApiResponseService> apiResponseServiceMockedStatic;

    private AuthAdminRequest request;
    private AuthAdminInput input;
    private AuthAdminOutput output;
    private AuthAdminResponse responseDto;
    private ResponseEntity<SuccessResponse<AuthAdminResponse>> expectedResponseEntity;

    @BeforeEach
    void setUp() {
        authAdminMapperMockedStatic = mockStatic(AuthAdminMapper.class);
        apiResponseServiceMockedStatic = mockStatic(ApiResponseService.class);

        request = new AuthAdminRequest("admin@letraaletra.com", "SecurePass2026!");
        input = new AuthAdminInput("admin@letraaletra.com", "SecurePass2026!");

        UUID adminId = UUID.randomUUID();
        String generatedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken";
        output = new AuthAdminOutput(adminId, generatedToken);
        responseDto = new AuthAdminResponse(adminId.toString(), generatedToken);

        SuccessResponse<AuthAdminResponse> successResponse = new SuccessResponse<>(true, responseDto);
        expectedResponseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @AfterEach
    void tearDown() {
        authAdminMapperMockedStatic.close();
        apiResponseServiceMockedStatic.close();
    }

    @Test
    @DisplayName("Should successfully authenticate admin and return 200 OK with data")
    void shouldAuthenticateAdminSuccessfully() {
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toInput(request)).thenReturn(input);
        when(useCase.execute(input)).thenReturn(output);
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toResponse(output)).thenReturn(responseDto);
        apiResponseServiceMockedStatic.when(() -> ApiResponseService.success(responseDto)).thenReturn(expectedResponseEntity);

        ResponseEntity<SuccessResponse<AuthAdminResponse>> result = authAdminController.handle(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponseEntity, result);

        verify(useCase, times(1)).execute(input);
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toInput(request), times(1));
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toResponse(output), times(1));
        apiResponseServiceMockedStatic.verify(() -> ApiResponseService.success(responseDto), times(1));
    }

    @Test
    @DisplayName("Should propagate AdminNotFoundException when usecase layer target admin does not exist")
    void shouldPropagateAdminNotFoundExceptionFromUseCase() {
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toInput(request)).thenReturn(input);
        when(useCase.execute(input)).thenThrow(new AdminNotFoundException());

        assertThrows(AdminNotFoundException.class, () -> authAdminController.handle(request));

        verify(useCase, times(1)).execute(input);
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toInput(request), times(1));
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toResponse(any()), never());
        apiResponseServiceMockedStatic.verify(() -> ApiResponseService.success(any()), never());
    }

    @Test
    @DisplayName("Should propagate InvalidPasswordException when usecase password mismatch occurs")
    void shouldPropagateInvalidPasswordExceptionFromUseCase() {
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toInput(request)).thenReturn(input);
        when(useCase.execute(input)).thenThrow(new InvalidPasswordException());

        assertThrows(InvalidPasswordException.class, () -> authAdminController.handle(request));

        verify(useCase, times(1)).execute(input);
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toInput(request), times(1));
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toResponse(any()), never());
        apiResponseServiceMockedStatic.verify(() -> ApiResponseService.success(any()), never());
    }

    @Test
    @DisplayName("Should throw NullPointerException when incoming web request payload is completely null (Expected Missing Behavior)")
    void shouldThrowExceptionWhenPayloadIsNull() {
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toInput(null)).thenThrow(new NullPointerException("Request body cannot be null"));

        assertThrows(NullPointerException.class, () -> authAdminController.handle(null));

        verifyNoInteractions(useCase);
    }

    @Test
    @DisplayName("Should propagate global runtime exceptions thrown during structural entity mapping operations")
    void shouldPropagateRuntimeExceptionsFromMapper() {
        authAdminMapperMockedStatic.when(() -> AuthAdminMapper.toInput(request))
                .thenThrow(new IllegalArgumentException("Mapping error due to malformed metadata"));

        assertThrows(IllegalArgumentException.class, () -> authAdminController.handle(request));

        verifyNoInteractions(useCase);
        authAdminMapperMockedStatic.verify(() -> AuthAdminMapper.toInput(request), times(1));
    }
}