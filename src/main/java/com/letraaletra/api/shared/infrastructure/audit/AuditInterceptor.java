package com.letraaletra.api.shared.infrastructure.audit;

import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.slf4j.event.Level;
import org.springframework.web.method.HandlerMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.slf4j.event.Level.*;

@Component
public class AuditInterceptor implements HandlerInterceptor {
    private final AuditService auditService;

    public AuditInterceptor(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable Exception ex
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        AuthenticatedUser user = getAuthenticatedUser();
        String username = user == null ? "anonymous" : user.name();
        String action = getAction(handlerMethod);
        String resourceId = getResourceId(request);

        if (user != null && user.isAdmin()) {
            auditService.admin(
                    getLevel(response),
                    "{} {} {} ({})",
                    username,
                    action,
                    resourceId,
                    response.getStatus()
            );
        } else {
            auditService.game(
                    getLevel(response),
                    "{} {} {} ({})",
                    username,
                    action,
                    resourceId,
                    response.getStatus()
            );
        }
    }

    private AuthenticatedUser getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }

        return user;
    }

    private String getAction(HandlerMethod handlerMethod) {

        String name = handlerMethod
                .getBeanType()
                .getSimpleName();

        return name
                .replace("Controller", "")
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .toLowerCase();
    }

    @SuppressWarnings("unchecked")
    private String getResourceId(HttpServletRequest request) {

        Map<String, String> variables =
                (Map<String, String>) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
                );

        if (variables == null || variables.isEmpty()) {
            return "";
        }

        return String.join(" ", variables.values());
    }

    private Level getLevel(HttpServletResponse response) {

        int status = response.getStatus();

        if (status >= 500) {
            return ERROR;
        }

        if (status >= 400) {
            return WARN;
        }

        return INFO;
    }
}
