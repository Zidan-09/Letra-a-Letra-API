package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.GetTicketsMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/ticket")
@Tag(name = "Ticket Admin", description = "Rotas administrativas de gerenciamento de tickets")
public class GetTicketsController {
    private final UseCase<GetTicketsInput, GetTicketsOutput> useCase;

    @GetMapping
    public ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        GetTicketsInput input = GetTicketsMapper.toInput(
                principal,
                status,
                category,
                userId,
                page,
                size,
                "ASC".equalsIgnoreCase(direction)
        );

        GetTicketsOutput output = useCase.execute(input);

        PageResponse<TicketResponse> dto = GetTicketsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
