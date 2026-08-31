package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.application.output.ResolveTicketOutput;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.ResolveTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ResolveTicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.ResolveTicketMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
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
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveTicketControllerTest {

    @Mock
    private UseCase<ResolveTicketInput, ResolveTicketOutput> useCase;

    @InjectMocks
    private ResolveTicketController controller;

    private AuthenticatedUser adminPrincipal;
    private UUID ticketId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        adminPrincipal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        ownerId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
    }

    private TicketDetails buildTicketDetails() {
        return new TicketDetails(
                ticketId,
                ownerId,
                "player",
                TicketCategory.BUG,
                TicketStatus.RESOLVED,
                "Cannot login",
                "The game crashes when I try to login",
                "Fixed in patch 1.2",
                adminPrincipal.auth(),
                "Admin",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should delegate resolution with the request note and return the updated ticket")
    void shouldResolveTicket() {
        ResolveTicketRequest request = new ResolveTicketRequest("Fixed in patch 1.2");
        TicketDetails resolved = buildTicketDetails();
        ResolveTicketInput input = new ResolveTicketInput(adminPrincipal, ticketId, "Fixed in patch 1.2");
        ResolveTicketOutput output = new ResolveTicketOutput(resolved);
        ResolveTicketResponse responseDto = new ResolveTicketResponse(
                new TicketResponse(
                        resolved.ticketId(),
                        resolved.userId(),
                        resolved.username(),
                        resolved.category(),
                        resolved.status(),
                        resolved.subject(),
                        resolved.description(),
                        resolved.resolutionNote(),
                        resolved.resolvedByAdminId(),
                        resolved.adminName(),
                        resolved.resolvedAt(),
                        resolved.createdAt()
                )
        );
        ResponseEntity<SuccessResponse<ResolveTicketResponse>> expected = ApiResponseHandler.success(responseDto);

        try (MockedStatic<ResolveTicketMapper> mapperMock = mockStatic(ResolveTicketMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> ResolveTicketMapper.toInput(adminPrincipal, ticketId, request)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(output);
            mapperMock.when(() -> ResolveTicketMapper.toResponse(output)).thenReturn(responseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(responseDto)).thenReturn(expected);

            ResponseEntity<SuccessResponse<ResolveTicketResponse>> response =
                    controller.handle(adminPrincipal, ticketId, request);

            assertEquals(expected, response);
            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should accept a missing body mapping it to a null note")
    void shouldAcceptMissingBody() {
        ResolveTicketInput input = new ResolveTicketInput(adminPrincipal, ticketId, null);
        TicketDetails resolved = buildTicketDetails();
        ResolveTicketResponse responseDto = new ResolveTicketResponse(
                new TicketResponse(
                        resolved.ticketId(),
                        resolved.userId(),
                        resolved.username(),
                        resolved.category(),
                        resolved.status(),
                        resolved.subject(),
                        resolved.description(),
                        resolved.resolutionNote(),
                        resolved.resolvedByAdminId(),
                        resolved.adminName(),
                        resolved.resolvedAt(),
                        resolved.createdAt()
                )
        );

        try (MockedStatic<ResolveTicketMapper> mapperMock = mockStatic(ResolveTicketMapper.class)) {

            mapperMock.when(() -> ResolveTicketMapper.toInput(adminPrincipal, ticketId, null)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(new ResolveTicketOutput(resolved));
            mapperMock.when(() -> ResolveTicketMapper.toResponse(org.mockito.ArgumentMatchers.any(ResolveTicketOutput.class)))
                    .thenReturn(responseDto);

            controller.handle(adminPrincipal, ticketId, null);

            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should propagate authorization and business failures")
    void shouldPropagateExceptions() {
        try (MockedStatic<ResolveTicketMapper> mapperMock = mockStatic(ResolveTicketMapper.class)) {
            ResolveTicketInput input = new ResolveTicketInput(adminPrincipal, ticketId, null);
            mapperMock.when(() -> ResolveTicketMapper.toInput(adminPrincipal, ticketId, null)).thenReturn(input);
            when(useCase.execute(input)).thenThrow(new IllegalStateException("already resolved"));

            assertThrows(IllegalStateException.class, () -> controller.handle(adminPrincipal, ticketId, null));
        }
    }
}
