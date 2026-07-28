package com.letraaletra.api.features.transaction.infrastructure.controller;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserNicknameInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserNicknameOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.FindTransactionsByUserNicknameMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/transaction")
@Tag(name = "Transaction", description = "Rotas relacionadas a funcionalidade de transações dos usuários")
public class FindTransactionByUserNicknameController {
    private final UseCase<FindTransactionsByUserNicknameInput, FindTransactionsByUserNicknameOutput> useCase;

    public FindTransactionByUserNicknameController(
            UseCase<FindTransactionsByUserNicknameInput, FindTransactionsByUserNicknameOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/user/username/{username}")
    public ResponseEntity<SuccessResponse<PageResponse<TransactionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String username,
            Pageable pageable
    ) {
        FindTransactionsByUserNicknameInput input = FindTransactionsByUserNicknameMapper.toInput(principal, username, pageable);

        FindTransactionsByUserNicknameOutput output = useCase.execute(input);

        PageResponse<TransactionResponse> dto = FindTransactionsByUserNicknameMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
