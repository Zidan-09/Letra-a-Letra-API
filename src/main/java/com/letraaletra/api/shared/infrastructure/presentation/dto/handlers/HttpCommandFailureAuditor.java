package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import jakarta.servlet.http.HttpServletRequest;

public interface HttpCommandFailureAuditor {
    void recordFailure(HttpServletRequest request, Exception ex, int status);
}
