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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ticket")
@Tag(name = "Ticket", description = "Rotas de criação e consulta de tickets de suporte pelo usuário")
public class GetMyTicketsController {
    private final UseCase<GetMyTicketsInput, GetMyTicketsOutput> useCase;

    @GetMapping(path = "/my")
    public ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetMyTicketsInput input = GetMyTicketsMapper.toInput(
                principal,
                pageable
        );

        GetMyTicketsOutput output = useCase.execute(input);

        PageResponse<TicketResponse> dto = GetMyTicketsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
