package com.letraaletra.api.shared.infrastructure.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestCorrelationFilter extends OncePerRequestFilter {

    private final MdcOperationContext operationContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        String sourceDetail = request.getMethod() + " " + request.getRequestURI();

        operationContext.bindRequest(requestId, sourceDetail);

        try {
            filterChain.doFilter(request, response);
        } finally {
            operationContext.unbindRequest();
        }
    }
}
