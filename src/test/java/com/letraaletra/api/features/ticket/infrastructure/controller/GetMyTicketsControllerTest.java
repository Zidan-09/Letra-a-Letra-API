package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.GetMyTicketsMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMyTicketsControllerTest {

    @Mock
    private UseCase<GetMyTicketsInput, GetMyTicketsOutput> useCase;

    @InjectMocks
    private GetMyTicketsController controller;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
    }

    @Test
    @DisplayName("Should resolve the user from the principal and return the paged response")
    void shouldReturnPagedOwnTickets() {
        Pageable pageable = PageRequest.of(0, 20);
        GetMyTicketsInput input = new GetMyTicketsInput(principal.auth(), 0, 20, pageable.getSort());
        PageResponse<TicketResponse> dto = new PageResponse<>(List.of(), 0, 20, 0, 0, true, true);
        ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> expected = ApiResponseHandler.success(dto);

        try (MockedStatic<GetMyTicketsMapper> mapperMock = mockStatic(GetMyTicketsMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetMyTicketsMapper.toInput(principal, pageable)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(new GetMyTicketsOutput(org.springframework.data.domain.Page.empty()));
            mapperMock.when(() -> GetMyTicketsMapper.toResponse(org.mockito.ArgumentMatchers.any(GetMyTicketsOutput.class)))
                    .thenReturn(dto);
            apiResponseMock.when(() -> ApiResponseHandler.success(dto)).thenReturn(expected);

            ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> response =
                    controller.handle(principal, pageable);

            assertEquals(expected, response);
            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should map ASC direction to ascending ordering")
    void shouldMapAscDirection() {
        Pageable pageable = PageRequest.of(1, 5, org.springframework.data.domain.Sort.by("createdAt").ascending());
        GetMyTicketsInput input = new GetMyTicketsInput(principal.auth(), 1, 5, pageable.getSort());

        try (MockedStatic<GetMyTicketsMapper> mapperMock = mockStatic(GetMyTicketsMapper.class);
             MockedStatic<ApiResponseHandler> ignored = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetMyTicketsMapper.toInput(principal, pageable)).thenReturn(input);
            when(useCase.execute(input)).thenReturn(new GetMyTicketsOutput(org.springframework.data.domain.Page.empty()));
            mapperMock.when(() -> GetMyTicketsMapper.toResponse(org.mockito.ArgumentMatchers.any(GetMyTicketsOutput.class)))
                    .thenReturn(new PageResponse<>(List.of(), 1, 5, 0, 0, false, true));

            controller.handle(principal, pageable);

            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should propagate use case exceptions")
    void shouldPropagateExceptions() {
        try (MockedStatic<GetMyTicketsMapper> mapperMock = mockStatic(GetMyTicketsMapper.class)) {
            Pageable pageable = PageRequest.of(0, 20);
            GetMyTicketsInput input = new GetMyTicketsInput(principal.auth(), 0, 20, pageable.getSort());
            mapperMock.when(() -> GetMyTicketsMapper.toInput(principal, pageable)).thenReturn(input);
            when(useCase.execute(input)).thenThrow(new IllegalArgumentException("boom"));

            assertThrows(IllegalArgumentException.class, () -> controller.handle(principal, pageable));
        }
    }
}
