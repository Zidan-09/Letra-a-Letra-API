package com.letraaletra.api.shared.application.port;

import java.util.Optional;
import java.util.UUID;

public interface OperationContext {
    Optional<String> currentRequestId();

    Optional<UUID> currentOperationId();

    Optional<String> currentCorrelationId();

    void runAsOperation(UUID operationId, Runnable action);

    void runAsOperation(UUID operationId, String correlationId, Runnable action);
}
