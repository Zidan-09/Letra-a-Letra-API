package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.application.output.ResolveTicketOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.ResolveTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ResolveTicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.ResolveTicketMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/ticket")
@Tag(name = "Ticket Admin", description = "Rotas administrativas de gerenciamento de tickets")
public class ResolveTicketController {
    private final UseCase<ResolveTicketInput, ResolveTicketOutput> useCase;

    @Transactional
    @PatchMapping(path = "/{ticketId}/resolve")
    public ResponseEntity<SuccessResponse<ResolveTicketResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID ticketId,
            @Valid @RequestBody(required = false) ResolveTicketRequest request
    ) {
        ResolveTicketInput input = ResolveTicketMapper.toInput(principal, ticketId, request);

        ResolveTicketOutput output = useCase.execute(input);

        ResolveTicketResponse dto = ResolveTicketMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
