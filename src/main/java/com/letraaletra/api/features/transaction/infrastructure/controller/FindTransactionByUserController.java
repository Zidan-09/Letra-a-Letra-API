package com.letraaletra.api.features.transaction.infrastructure.controller;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.FindTransactionsByUserMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/transaction")
@Tag(name = "Transaction", description = "Rotas relacionadas a funcionalidade de transações dos usuários")
public class FindTransactionByUserController {
    private final UseCase<FindTransactionsByUserInput, FindTransactionsByUserOutput> useCase;

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<SuccessResponse<PageResponse<TransactionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID userId,
            Pageable pageable
    ) {
        FindTransactionsByUserInput input = FindTransactionsByUserMapper.toInput(principal, userId, pageable);

        FindTransactionsByUserOutput output = useCase.execute(input);

        PageResponse<TransactionResponse> dto = FindTransactionsByUserMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
