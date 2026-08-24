package com.letraaletra.api.shared.infrastructure.audit;

import com.letraaletra.api.shared.application.port.OperationContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MdcOperationContext implements OperationContext {

    public static final String REQUEST_ID_KEY = "auditRequestId";
    public static final String OPERATION_ID_KEY = "auditOperationId";
    public static final String CORRELATION_ID_KEY = "auditCorrelationId";
    public static final String SOURCE_DETAIL_KEY = "auditSourceDetail";

    @Override
    public Optional<String> currentRequestId() {
        return Optional.ofNullable(MDC.get(REQUEST_ID_KEY));
    }

    @Override
    public Optional<UUID> currentOperationId() {
        return parseUuid(MDC.get(OPERATION_ID_KEY));
    }

    @Override
    public Optional<String> currentCorrelationId() {
        return Optional.ofNullable(MDC.get(CORRELATION_ID_KEY));
    }

    @Override
    public void runAsOperation(UUID operationId, Runnable action) {
        runAsOperation(operationId, null, action);
    }

    @Override
    public void runAsOperation(UUID operationId, String correlationId, Runnable action) {
        String previousOperation = MDC.get(OPERATION_ID_KEY);
        String previousCorrelation = MDC.get(CORRELATION_ID_KEY);

        try {
            MDC.put(OPERATION_ID_KEY, operationId.toString());

            if (correlationId != null) {
                MDC.put(CORRELATION_ID_KEY, correlationId);
            }

            action.run();
        } finally {
            restore(OPERATION_ID_KEY, previousOperation);
            restore(CORRELATION_ID_KEY, previousCorrelation);
        }
    }

    public void bindRequest(String requestId, String sourceDetail) {
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(SOURCE_DETAIL_KEY, sourceDetail);
    }

    public void unbindRequest() {
        MDC.remove(REQUEST_ID_KEY);
        MDC.remove(SOURCE_DETAIL_KEY);
    }

    private void restore(String key, String previousValue) {
        if (previousValue == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, previousValue);
        }
    }

    private Optional<UUID> parseUuid(String value) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
