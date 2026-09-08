package com.letraaletra.api.shared.infrastructure.transaction;

import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionExecutor implements TransactionalExecutorService {
    private final TransactionTemplate transactionTemplate;

    @Override
    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}
