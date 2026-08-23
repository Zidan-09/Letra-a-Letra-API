package com.letraaletra.api.features.audit.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.audit.application.input.GetAuditEventsInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetAuditEventsUseCase implements UseCase<GetAuditEventsInput, GetAuditEventsOutput> {

    private static final int MAX_PAGE_SIZE = 200;

    private final FindAuditEvents findAuditEvents;
    private final AdminChecker adminChecker;

    public GetAuditEventsUseCase(FindAuditEvents findAuditEvents, AdminChecker adminChecker) {
        this.findAuditEvents = findAuditEvents;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetAuditEventsOutput execute(GetAuditEventsInput input) {
        adminChecker.check(input.principal(), PermissionKey.AUDIT, PermissionAction.VIEW);

        int size = Math.min(Math.max(input.size(), 1), MAX_PAGE_SIZE);
        int page = Math.max(input.page(), 0);

        return new GetAuditEventsOutput(
                findAuditEvents.find(input.toFilter(), page, size, input.ascending())
        );
    }
}
