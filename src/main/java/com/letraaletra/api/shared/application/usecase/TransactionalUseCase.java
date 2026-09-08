package com.letraaletra.api.shared.application.usecase;

import com.letraaletra.api.shared.application.port.TransactionalExecutorService;

import java.util.Collections;
import java.util.Set;

public class TransactionalUseCase<I, O> implements UseCase<I, O> {
    private final UseCase<I, O> delegate;
    private final TransactionalExecutorService transactions;
    private final Set<Class<? extends RuntimeException>> commitOn;

    public TransactionalUseCase(
            UseCase<I, O> delegate,
            TransactionalExecutorService transactions
    ) {
        this(delegate, transactions, Collections.emptySet());
    }

    public TransactionalUseCase(
            UseCase<I, O> delegate,
            TransactionalExecutorService transactions,
            Set<Class<? extends RuntimeException>> commitOn
    ) {
        this.delegate = delegate;
        this.transactions = transactions;
        this.commitOn = commitOn == null ? Collections.emptySet() : Set.copyOf(commitOn);
    }

    @Override
    public O execute(I input) {
        if (commitOn.isEmpty()) {
            return transactions.execute(() -> delegate.execute(input));
        }

        Outcome<O> outcome = transactions.execute(() -> {
            try {
                return Outcome.ok(delegate.execute(input));
            } catch (RuntimeException e) {
                if (isCommitOn(e)) {
                    return Outcome.err(e);
                }
                throw e;
            }
        });

        if (outcome.error() != null) {
            throw outcome.error();
        }
        return outcome.value();
    }

    private boolean isCommitOn(RuntimeException e) {
        for (Class<? extends RuntimeException> type : commitOn) {
            if (type.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    private record Outcome<O>(O value, RuntimeException error) {
        static <O> Outcome<O> ok(O value) {
            return new Outcome<>(value, null);
        }

        static <O> Outcome<O> err(RuntimeException error) {
            return new Outcome<>(null, error);
        }
    }
}
