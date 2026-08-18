package com.letraaletra.api.features.game.infrastructure.transaction;

import com.letraaletra.api.features.game.application.port.TransactionalExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionExecutor implements TransactionalExecutorService {
    private final TransactionTemplate transactionTemplate;

    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}
