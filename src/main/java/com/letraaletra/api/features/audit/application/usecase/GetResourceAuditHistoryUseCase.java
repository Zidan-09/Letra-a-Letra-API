package com.letraaletra.api.features.audit.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.audit.application.input.GetResourceAuditHistoryInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.exception.InvalidAuditResourceTypeException;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetResourceAuditHistoryUseCase implements UseCase<GetResourceAuditHistoryInput, GetAuditEventsOutput> {

    private static final int MAX_PAGE_SIZE = 200;

    private final FindAuditEvents findAuditEvents;
    private final AdminChecker adminChecker;

    public GetResourceAuditHistoryUseCase(FindAuditEvents findAuditEvents, AdminChecker adminChecker) {
        this.findAuditEvents = findAuditEvents;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetAuditEventsOutput execute(GetResourceAuditHistoryInput input) {
        adminChecker.check(input.principal(), PermissionKey.AUDIT, PermissionAction.VIEW);

        AuditEventFilter filter = new AuditEventFilter(
                input.targetUserId(),
                null,
                null,
                null,
                null,
                parseResourceType(input.resourceType()),
                input.resourceId(),
                input.from(),
                input.to(),
                null,
                null,
                null,
                null
        );

        int size = Math.min(Math.max(input.size(), 1), MAX_PAGE_SIZE);
        int page = Math.max(input.page(), 0);

        return new GetAuditEventsOutput(findAuditEvents.find(filter, page, size, false));
    }

    private AuditResourceType parseResourceType(String raw) {
        try {
            return AuditResourceType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ignored) {
            throw new InvalidAuditResourceTypeException();
        }
    }
}
