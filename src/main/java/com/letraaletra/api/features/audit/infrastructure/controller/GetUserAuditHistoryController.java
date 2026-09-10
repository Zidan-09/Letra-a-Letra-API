package com.letraaletra.api.features.audit.infrastructure.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letraaletra.api.features.audit.application.input.GetUserAuditHistoryInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.infrastructure.presentation.dto.response.AuditEventResponse;
import com.letraaletra.api.features.audit.infrastructure.presentation.mapper.AuditEventResponseMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/audit")
@Tag(name = "Audit", description = "Rotas administrativas de consulta da auditoria de negócio")
public class GetUserAuditHistoryController {

    private final UseCase<GetUserAuditHistoryInput, GetAuditEventsOutput> useCase;

    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponse<PageResponse<AuditEventResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID userId,
            @RequestParam(required = false) AuditEventType eventType,
            @RequestParam(required = false) AuditCategory category,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        GetUserAuditHistoryInput input = new GetUserAuditHistoryInput(
                principal,
                userId,
                eventType,
                category,
                from,
                to,
                Pageables.clampPage(page),
                Pageables.clampSize(size)
        );

        GetAuditEventsOutput output = useCase.execute(input);

        return ApiResponseHandler.success(AuditEventResponseMapper.toPageResponse(output));
    }
}
