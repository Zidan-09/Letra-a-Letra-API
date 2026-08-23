package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.user.application.input.RevokeUserCosmeticInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.UUID;

public class RevokeUserCosmeticUseCase implements UseCase<RevokeUserCosmeticInput, Void> {
    private static final String SOURCE_DETAIL = "REVOKE_USER_COSMETIC";

    private final UserRepository userRepository;
    private final AdminChecker adminChecker;
    private final BusinessAuditRecorder auditRecorder;

    public RevokeUserCosmeticUseCase(
            UserRepository userRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.adminChecker = adminChecker;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public Void execute(RevokeUserCosmeticInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        UUID operationId = UUID.randomUUID();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        var movements = user.getInventory().removeFromInventory(input.cosmeticId());

        userRepository.save(user);

        AuditEventFactory.inventoryChanges(
                movements,
                input.userId(),
                new AuditActor(AuditActorType.ADMIN, input.principal().auth(), input.principal().name()),
                "ADMIN_REVOKE",
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);

        return null;
    }
}
