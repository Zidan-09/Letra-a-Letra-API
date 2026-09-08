package com.letraaletra.api.shared.application.port;

import java.util.function.Supplier;

public interface TransactionalExecutorService {
    <T> T execute(Supplier<T> action);
}
