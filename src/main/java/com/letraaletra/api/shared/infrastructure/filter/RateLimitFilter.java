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

    private static final int MAX_REQUESTS_PER_MINUTE = 30;
    private static final long WINDOW_MS = 60_000L;
    private static final int MAX_TRACKED_WINDOWS = 2_000;

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
            start = w.windowStart.get();
        }
        int current = w.count.incrementAndGet();
        evictExpiredWindows(now);
        if (current > MAX_REQUESTS_PER_MINUTE) {
            long retryAfterSeconds = Math.max(1L, (WINDOW_MS - (now - start) + 999L) / 1000L);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
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
        if ("POST".equalsIgnoreCase(method)) {
            if (isPasswordResetPath(path)) return true;
            if (path.equals("/user")) return true;
            if (path.startsWith("/user/auth")) return true;
            if (path.startsWith("/admin/auth")) return true;
            if (path.equals("/ticket")) return true;
            if (path.equals("/friend/request")) return true;
            if (path.equals("/cosmetic")) return true;
            if (path.startsWith("/shop/offers/")) return true;
        }
        return isMutating(method)
                && (path.equals("/admin") || path.startsWith("/admin/"));
    }

    private boolean isPasswordResetPath(String path) {
        return path.equals("/user/auth/forgot-password")
                || path.equals("/user/auth/verify-reset-code")
                || path.equals("/user/auth/reset-password")
                || path.equals("/admin/auth/forgot-password")
                || path.equals("/admin/auth/verify-reset-token")
                || path.equals("/admin/auth/reset-password");
    }

    private boolean isMutating(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }

    private void evictExpiredWindows(long now) {
        if (windows.size() <= MAX_TRACKED_WINDOWS) return;
        long cutoff = now - WINDOW_MS;
        windows.entrySet().removeIf(entry -> entry.getValue().windowStart.get() < cutoff);
    }

    private String buildKey(HttpServletRequest request) {
        return request.getRemoteAddr() + ":" + request.getRequestURI();
    }
}
