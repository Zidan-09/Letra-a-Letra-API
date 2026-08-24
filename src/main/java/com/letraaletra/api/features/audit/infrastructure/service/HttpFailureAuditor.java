package com.letraaletra.api.features.audit.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.HttpCommandFailureAuditor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class HttpFailureAuditor implements HttpCommandFailureAuditor {

    private static final Set<String> AUDITED_HTTP_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final BusinessAuditRecorder auditRecorder;

    @Override
    public void recordFailure(HttpServletRequest request, Exception ex, int status) {
        try {
            if (!AUDITED_HTTP_METHODS.contains(request.getMethod())) {
                return;
            }

            Throwable rootCause = ex;

            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            auditRecorder.recordFailure(AuditEvent.builder()
                    .category(AuditCategory.OPERATION)
                    .eventType(AuditEventType.COMMAND_FAILED)
                    .actor(requestActor())
                    .resourceType(AuditResourceType.USER)
                    .resourceId(resolveResourceId())
                    .failureReason(rootCause.getMessage())
                    .sourceType(AuditSourceType.HTTP)
                    .metadata(Map.of(
                            "status", status,
                            "exceptionType", rootCause.getClass().getSimpleName()
                    ))
                    .build());
        } catch (Exception ignored) {
            // FAILURE recording is best-effort; never changes the original response.
        }
    }

    private AuditActor requestActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            AuditActorType type = user.isAdmin() ? AuditActorType.ADMIN : AuditActorType.USER;

            return new AuditActor(type, user.auth(), user.name());
        }

        return new AuditActor(AuditActorType.SYSTEM, null, "ANONYMOUS");
    }

    private String resolveResourceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user.auth().toString();
        }

        return "anonymous";
    }
}
