package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.GrantUserRewardRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
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

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GrantUserRewardControllerTest {
    @Mock
    private UseCase<GrantUserRewardInput, Void> useCase;

    @InjectMocks
    private GrantUserRewardController controller;

    @Test
    @DisplayName("Deve delegar a concessão de recompensa ao caso de uso e retornar NO_CONTENT")
    void handle_WhenRequestIsValid_ShouldDelegateToUseCaseAndReturnNoContent() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "AdminUser", true, false);
        UUID userId = UUID.randomUUID();
        GrantUserRewardRequest request = new GrantUserRewardRequest(RewardType.COIN, 100, null);

        Mockito.when(useCase.execute(Mockito.any(GrantUserRewardInput.class)))
                .thenReturn(null);

        ResponseEntity<SuccessResponse<Void>> response = controller.handle(principal, userId, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Mockito.verify(useCase, Mockito.times(1))
                .execute(Mockito.any(GrantUserRewardInput.class));
    }
}
