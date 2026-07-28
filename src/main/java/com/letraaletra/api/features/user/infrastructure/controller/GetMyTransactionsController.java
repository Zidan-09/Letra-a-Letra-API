package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.features.user.application.input.GetMyTransactionsInput;
import com.letraaletra.api.features.user.application.output.GetMyTransactionsOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.GetMyTransactionsMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class GetMyTransactionsController {
    private final UseCase<GetMyTransactionsInput, GetMyTransactionsOutput> useCase;

    public GetMyTransactionsController(
            UseCase<GetMyTransactionsInput, GetMyTransactionsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/transactions")
    public ResponseEntity<SuccessResponse<PageResponse<TransactionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
            GetMyTransactionsInput input = GetMyTransactionsMapper.toInput(principal, pageable);

            GetMyTransactionsOutput output = useCase.execute(input);

            PageResponse<TransactionResponse> dto = GetMyTransactionsMapper.toResponse(output);

            return ApiResponseHandler.success(dto);
    }
}
