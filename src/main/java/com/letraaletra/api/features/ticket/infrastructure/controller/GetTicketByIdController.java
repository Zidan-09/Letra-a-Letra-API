package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketByIdOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.GetTicketByIdMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ticket")
@Tag(name = "Ticket", description = "Rotas de criação e consulta de tickets de suporte pelo usuário")
public class GetTicketByIdController {
    private final UseCase<GetTicketByIdInput, GetTicketByIdOutput> useCase;

    @GetMapping(path = "/{ticketId}")
    public ResponseEntity<SuccessResponse<TicketResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID ticketId
    ) {
        GetTicketByIdInput input = GetTicketByIdMapper.toInput(principal, ticketId);

        GetTicketByIdOutput output = useCase.execute(input);

        TicketResponse dto = GetTicketByIdMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
