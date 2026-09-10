package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.CreateTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.CreateTicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.CreateTicketMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ticket")
@Tag(name = "Ticket", description = "Rotas de criação e consulta de tickets de suporte pelo usuário")
public class CreateTicketController {
    private final UseCase<CreateTicketInput, CreateTicketOutput> useCase;

    @PostMapping
    public ResponseEntity<SuccessResponse<CreateTicketResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CreateTicketRequest request
    ) {
        CreateTicketInput input = CreateTicketMapper.toInput(principal, request);

        CreateTicketOutput output = useCase.execute(input);

        CreateTicketResponse dto = CreateTicketMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
