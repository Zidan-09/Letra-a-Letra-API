package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.CreateTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.CreateTicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.CreateTicketMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTicketControllerTest {

    @Mock
    private UseCase<CreateTicketInput, CreateTicketOutput> useCase;

    @InjectMocks
    private CreateTicketController controller;

    private AuthenticatedUser principal;
    private CreateTicketRequest request;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
        request = new CreateTicketRequest(TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");
        ticket = Ticket.create(principal.auth(), TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");
    }

    @Test
    @DisplayName("Should map request, execute use case and return wrapped success response")
    void shouldCreateTicketSuccessfully() {
        CreateTicketInput input = new CreateTicketInput(principal, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");
        CreateTicketOutput output = new CreateTicketOutput(ticket);
        CreateTicketResponse responseDto = new CreateTicketResponse(
                new com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse(
                        ticket.getTicketId(),
                        ticket.getUserId(),
                        ticket.getCategory(),
                        ticket.getStatus(),
                        ticket.getSubject(),
                        ticket.getDescription(),
                        null,
                        null,
                        null,
                        ticket.getCreatedAt()
                )
        );
        ResponseEntity<SuccessResponse<CreateTicketResponse>> expected =
                ApiResponseHandler.success(responseDto);

        try (MockedStatic<CreateTicketMapper> mapperMock = mockStatic(CreateTicketMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> CreateTicketMapper.toInput(principal, request)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(output);
            mapperMock.when(() -> CreateTicketMapper.toResponse(output)).thenReturn(responseDto);
            apiResponseMock.when(() -> ApiResponseHandler.success(responseDto)).thenReturn(expected);

            ResponseEntity<SuccessResponse<CreateTicketResponse>> response = controller.handle(principal, request);

            assertEquals(expected, response);
            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should propagate business exceptions raised by the use case")
    void shouldPropagateUseCaseExceptions() {
        CreateTicketInput input = new CreateTicketInput(principal, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");

        try (MockedStatic<CreateTicketMapper> mapperMock = mockStatic(CreateTicketMapper.class)) {
            mapperMock.when(() -> CreateTicketMapper.toInput(principal, request)).thenReturn(input);
            when(useCase.execute(input)).thenThrow(new IllegalStateException("business failure"));

            assertThrows(IllegalStateException.class, () -> controller.handle(principal, request));
        }
    }
}
