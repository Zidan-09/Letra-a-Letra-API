package com.letraaletra.api.shared.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${rate-limit.enabled:true}")
    private boolean enabled;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    private static class Window {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
    }

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long WINDOW_MS = 60_000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enabled || !isProtected(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = buildKey(request);
        Window w = windows.computeIfAbsent(key, k -> new Window());
        long now = System.currentTimeMillis();
        long start = w.windowStart.get();
        if (now - start > WINDOW_MS) {
            synchronized (w) {
                if (now - w.windowStart.get() > WINDOW_MS) {
                    w.windowStart.set(now);
                    w.count.set(0);
                }
            }
        }
        int current = w.count.incrementAndGet();
        if (current > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Muitas requisições. Tente novamente em instantes.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtected(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path.startsWith("/user/forgot-password") && "POST".equalsIgnoreCase(method)) return true;
        if (path.startsWith("/user/verify-reset-code") && "POST".equalsIgnoreCase(method)) return true;
        if (path.startsWith("/user/reset-password") && "POST".equalsIgnoreCase(method)) return true;
        if (path.startsWith("/admin/forgot-password") && "POST".equalsIgnoreCase(method)) return true;
        if (path.startsWith("/user/auth") && "POST".equalsIgnoreCase(method)) return true;
        if (path.startsWith("/admin/auth") && "POST".equalsIgnoreCase(method)) return true;
        return path.equals("/user") && "POST".equalsIgnoreCase(method);
    }

    private String buildKey(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        else ip = ip.split(",")[0].trim();
        return ip + ":" + request.getRequestURI();
    }
}
