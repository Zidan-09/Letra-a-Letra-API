package com.letraaletra.api.features.transaction.infrastructure.controller;

import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.application.output.GetTransactionsOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.GetTransactionsMapper;
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
@RequestMapping(path = "/transaction")
@Tag(name = "Transaction", description = "Rotas relacionadas a funcionalidade de transações dos usuários")
public class GetTransactionsController {
    private final UseCase<GetTransactionsInput, GetTransactionsOutput> useCase;

    @GetMapping()
    public ResponseEntity<SuccessResponse<PageResponse<TransactionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetTransactionsInput input = GetTransactionsMapper.toInput(principal, pageable);

        GetTransactionsOutput output = useCase.execute(input);

        PageResponse<TransactionResponse> dto = GetTransactionsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
