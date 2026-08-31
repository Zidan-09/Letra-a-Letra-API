package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketByIdOutput;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.GetTicketByIdMapper;
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
class GetTicketByIdControllerTest {

    @Mock
    private UseCase<GetTicketByIdInput, GetTicketByIdOutput> useCase;

    @InjectMocks
    private GetTicketByIdController controller;

    private AuthenticatedUser principal;
    private UUID ticketId;
    private TicketDetails ticketDetails;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
        ticketId = UUID.randomUUID();
        ticketDetails = new TicketDetails(
                ticketId,
                principal.auth(),
                "player",
                TicketCategory.OTHER,
                TicketStatus.PENDING,
                "General doubt",
                "How does the matchmaking rating work exactly?",
                null,
                null,
                null,
                null,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should delegate to the use case and return the ticket response")
    void shouldReturnTicketById() {
        GetTicketByIdInput input = new GetTicketByIdInput(principal, ticketId);
        GetTicketByIdOutput output = new GetTicketByIdOutput(ticketDetails);
        TicketResponse responseDto = new TicketResponse(
                ticketDetails.ticketId(),
                ticketDetails.userId(),
                ticketDetails.username(),
                ticketDetails.category(),
                ticketDetails.status(),
                ticketDetails.subject(),
                ticketDetails.description(),
                ticketDetails.resolutionNote(),
                ticketDetails.resolvedByAdminId(),
                ticketDetails.adminName(),
                ticketDetails.resolvedAt(),
                ticketDetails.createdAt()
        );
        ResponseEntity<SuccessResponse<TicketResponse>> expected = ApiResponseHandler.success(responseDto);

        try (MockedStatic<GetTicketByIdMapper> mapperMock = mockStatic(GetTicketByIdMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetTicketByIdMapper.toInput(principal, ticketId)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(output);
            mapperMock.when(() -> GetTicketByIdMapper.toResponse(output)).thenReturn(responseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(responseDto)).thenReturn(expected);

            ResponseEntity<SuccessResponse<TicketResponse>> response = controller.handle(principal, ticketId);

            assertEquals(expected, response);
            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should propagate not found and authorization failures")
    void shouldPropagateExceptions() {
        try (MockedStatic<GetTicketByIdMapper> mapperMock = mockStatic(GetTicketByIdMapper.class)) {
            GetTicketByIdInput input = new GetTicketByIdInput(principal, ticketId);
            mapperMock.when(() -> GetTicketByIdMapper.toInput(principal, ticketId)).thenReturn(input);
            when(useCase.execute(input)).thenThrow(new IllegalArgumentException("not allowed"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal, ticketId));
        }
    }
}
