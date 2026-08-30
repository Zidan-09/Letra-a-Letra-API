package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.GetTicketsMapper;
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
class GetTicketsControllerTest {

    @Mock
    private UseCase<GetTicketsInput, GetTicketsOutput> useCase;

    @InjectMocks
    private GetTicketsController controller;

    private AuthenticatedUser adminPrincipal;

    @BeforeEach
    void setUp() {
        adminPrincipal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
    }

    @Test
    @DisplayName("Should forward status filter and pagination to the use case")
    void shouldForwardFiltersAndPagination() {
        Pageable pageable = PageRequest.of(0, 20);
        GetTicketsInput input = new GetTicketsInput(adminPrincipal, TicketStatus.PENDING, null, null, 0, 20, pageable.getSort());
        PageResponse<TicketResponse> dto = new PageResponse<>(List.of(), 0, 20, 0, 0, true, true);
        ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> expected = ApiResponseHandler.success(dto);

        try (MockedStatic<GetTicketsMapper> mapperMock = mockStatic(GetTicketsMapper.class);
             MockedStatic<ApiResponseHandler> apiResponseMock = mockStatic(ApiResponseHandler.class)) {

            mapperMock.when(() -> GetTicketsMapper.toInput(adminPrincipal, TicketStatus.PENDING, null, null, pageable))
                    .thenReturn(input);
            when(useCase.execute(input)).thenReturn(new GetTicketsOutput(org.springframework.data.domain.Page.empty()));
            mapperMock.when(() -> GetTicketsMapper.toResponse(org.mockito.ArgumentMatchers.any(GetTicketsOutput.class)))
                    .thenReturn(dto);
            apiResponseMock.when(() -> ApiResponseHandler.success(dto)).thenReturn(expected);

            ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> response =
                    controller.handle(adminPrincipal, TicketStatus.PENDING, null, null, pageable);

            assertEquals(expected, response);
            verify(useCase, times(1)).execute(input);
        }
    }

    @Test
    @DisplayName("Should propagate authorization failures")
    void shouldPropagateExceptions() {
        try (MockedStatic<GetTicketsMapper> mapperMock = mockStatic(GetTicketsMapper.class)) {
            Pageable pageable = PageRequest.of(0, 20);
            GetTicketsInput input = new GetTicketsInput(adminPrincipal, null, null, null, 0, 20, pageable.getSort());
            mapperMock.when(() -> GetTicketsMapper.toInput(adminPrincipal, null, null, null, pageable))
                    .thenReturn(input);
            when(useCase.execute(input)).thenThrow(new IllegalStateException("denied"));

            assertThrows(IllegalStateException.class,
                    () -> controller.handle(adminPrincipal, null, null, null, pageable));
        }
    }
}
