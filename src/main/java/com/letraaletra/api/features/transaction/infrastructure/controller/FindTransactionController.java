package com.letraaletra.api.features.transaction.infrastructure.controller;


import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.FindTransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.FindTransactionMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/transaction")
@Tag(name = "Transaction", description = "Rotas relacionadas a funcionalidade de transações dos usuários")
public class FindTransactionController {
    private final UseCase<FindTransactionInput, FindTransactionOutput> useCase;

    public FindTransactionController(
            UseCase<FindTransactionInput, FindTransactionOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/{transactionId}")
    public ResponseEntity<SuccessResponse<FindTransactionResponse>> handle(@PathVariable UUID transactionId) {
        FindTransactionInput input = FindTransactionMapper.toInput(transactionId);

        FindTransactionOutput output = useCase.execute(input);

        FindTransactionResponse dto = FindTransactionMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
