package com.letraaletra.api.features.transaction.infrastructure.controller;


import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.FindTransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.FindTransactionMapper;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
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
@RequestMapping(path = "/transaction")
@Tag(name = "Transaction", description = "Rotas relacionadas a funcionalidade de transações dos usuários")
public class FindTransactionController {
    private final UseCase<FindTransactionInput, FindTransactionOutput> useCase;

    @GetMapping(path = "/{transactionId}")
    public ResponseEntity<SuccessResponse<FindTransactionResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID transactionId
    ) {
        FindTransactionInput input = FindTransactionMapper.toInput(principal, transactionId);

        FindTransactionOutput output = useCase.execute(input);

        FindTransactionResponse dto = FindTransactionMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
