package com.letraaletra.api.features.audit.infrastructure.controller;

import com.letraaletra.api.features.audit.application.input.GetResourceAuditHistoryInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/audit")
@Tag(name = "Audit", description = "Rotas administrativas de consulta da auditoria de negócio")
public class GetResourceAuditHistoryController {

    private final UseCase<GetResourceAuditHistoryInput, GetAuditEventsOutput> useCase;

    @GetMapping("/resource/{type}/{id}")
    public ResponseEntity<SuccessResponse<PageResponse<AuditEventResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String type,
            @PathVariable String id,
            @RequestParam(required = false) UUID targetUserId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        GetResourceAuditHistoryInput input = new GetResourceAuditHistoryInput(
                principal,
                type,
                id,
                targetUserId,
                from,
                to,
                Pageables.clampPage(page),
                Pageables.clampSize(size)
        );

        GetAuditEventsOutput output = useCase.execute(input);

        return ApiResponseHandler.success(AuditEventResponseMapper.toPageResponse(output));
    }
}
