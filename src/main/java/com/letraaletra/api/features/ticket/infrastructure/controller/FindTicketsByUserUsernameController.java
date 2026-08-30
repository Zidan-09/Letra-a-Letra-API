package com.letraaletra.api.features.ticket.infrastructure.controller;

import com.letraaletra.api.features.ticket.application.input.FindTicketsByUserUsernameInput;
import com.letraaletra.api.features.ticket.application.output.FindTicketsByUserUsernameOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.features.ticket.infrastructure.presentation.mapper.FindTicketsByUserUsernameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/ticket")
@Tag(name = "Ticket Admin", description = "Rotas administrativas de gerenciamento de tickets")
public class FindTicketsByUserUsernameController {
    private final UseCase<FindTicketsByUserUsernameInput, FindTicketsByUserUsernameOutput> useCase;

    @GetMapping(path = "/user/username/{username}")
    public ResponseEntity<SuccessResponse<PageResponse<TicketResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String username,
            Pageable pageable
    ) {
        FindTicketsByUserUsernameInput input = FindTicketsByUserUsernameMapper.toInput(principal, username, pageable);

        FindTicketsByUserUsernameOutput output = useCase.execute(input);

        PageResponse<TicketResponse> dto = FindTicketsByUserUsernameMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}