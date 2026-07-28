package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetAdminTransactionsInput;
import com.letraaletra.api.features.admin.application.output.GetAdminTransactionsOutput;
import com.letraaletra.api.features.admin.domain.AdminTransactionsPage;
import com.letraaletra.api.features.admin.domain.repository.AdminTransactionRepository;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.transaction.AdminTransactionResponse;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetAdminTransactionsUseCase implements UseCase<GetAdminTransactionsInput, GetAdminTransactionsOutput> {
    private final AdminTransactionRepository adminTransactionRepository;
    private final AdminChecker adminChecker;

    public GetAdminTransactionsUseCase(
            AdminTransactionRepository adminTransactionRepository,
            AdminChecker adminChecker
    ) {
        this.adminTransactionRepository = adminTransactionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetAdminTransactionsOutput execute(GetAdminTransactionsInput input) {
        adminChecker.check(input.principal());

        Page<AdminTransactionResponse> transactionResponses = adminTransactionRepository.findAll(new AdminTransactionsPage(
                input.page(),
                input.size(),
                input.sort()
        ));

        return new GetAdminTransactionsOutput(transactionResponses);
    }
}
