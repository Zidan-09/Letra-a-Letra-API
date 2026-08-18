package com.letraaletra.api.features.game.application.port;

import java.util.function.Supplier;

public interface TransactionalExecutorService {
    <T> T execute(Supplier<T> action);
}
