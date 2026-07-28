package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.GetAdminTransactionsInput;
import com.letraaletra.api.features.admin.application.output.GetAdminTransactionsOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.GetAdminTransactionsMapper;
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
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class GetAdminTransactionsController {
    private final UseCase<GetAdminTransactionsInput, GetAdminTransactionsOutput> useCase;

    public GetAdminTransactionsController(
            UseCase<GetAdminTransactionsInput, GetAdminTransactionsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/transactions")
    public ResponseEntity<SuccessResponse<PageResponse<AdminTransactionResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetAdminTransactionsInput input = GetAdminTransactionsMapper.toInput(principal, pageable);

        GetAdminTransactionsOutput output = useCase.execute(input);

        PageResponse<AdminTransactionResponse> dto = GetAdminTransactionsMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
