package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ResetPasswordRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest {
    @Mock
    private UseCase<ResetPasswordInput, Void> useCase;

    @InjectMocks
    private ResetPasswordController controller;

    @Test
    @DisplayName("Deve delegar a redefinição de senha ao caso de uso e retornar NO_CONTENT")
    void handle_WhenRequestIsValid_ShouldDelegateToUseCaseAndReturnNoContent() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "user@example.com",
                "NewSecret123",
                "123456"
        );

        Mockito.when(useCase.execute(Mockito.any(ResetPasswordInput.class)))
                .thenReturn(null);

        ResponseEntity<SuccessResponse<Void>> response = controller.handle(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Mockito.verify(useCase, Mockito.times(1))
                .execute(Mockito.any(ResetPasswordInput.class));
    }
}
