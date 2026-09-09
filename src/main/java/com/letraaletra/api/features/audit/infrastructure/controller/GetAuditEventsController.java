package com.letraaletra.api.features.audit.infrastructure.controller;

import com.letraaletra.api.features.audit.application.input.GetAuditEventsInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.infrastructure.presentation.dto.response.AuditEventResponse;
import com.letraaletra.api.features.audit.infrastructure.presentation.mapper.AuditEventResponseMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/audit")
@Tag(name = "Audit", description = "Rotas administrativas de consulta da auditoria de negócio")
public class GetAuditEventsController {

    private final UseCase<GetAuditEventsInput, GetAuditEventsOutput> useCase;

    @GetMapping
    public ResponseEntity<SuccessResponse<PageResponse<AuditEventResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) UUID targetUserId,
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) AuditEventType eventType,
            @RequestParam(required = false) AuditCategory category,
            @RequestParam(required = false) AuditOutcome outcome,
            @RequestParam(required = false) AuditResourceType resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) UUID operationId,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) UUID transactionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        GetAuditEventsInput input = new GetAuditEventsInput(
                principal,
                targetUserId,
                actorId,
                eventType,
                category,
                outcome,
                resourceType,
                resourceId,
                from,
                to,
                requestId,
                operationId,
                correlationId,
                transactionId,
                Pageables.clampPage(page),
                Pageables.clampSize(size),
                "ASC".equalsIgnoreCase(direction)
        );

        GetAuditEventsOutput output = useCase.execute(input);

        return ApiResponseHandler.success(AuditEventResponseMapper.toPageResponse(output));
    }
}
