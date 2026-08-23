package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

import java.util.UUID;

public class ChangeCosmeticUseCase implements UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> {
    private static final String SOURCE_DETAIL = "CHANGE_COSMETIC";

    private final UserRepository userRepository;
    private final BusinessAuditRecorder auditRecorder;

    public ChangeCosmeticUseCase(UserRepository userRepository, BusinessAuditRecorder auditRecorder) {
        this.userRepository = userRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public ChangeCosmeticOutput execute(ChangeCosmeticInput input) {
        UUID operationId = UUID.randomUUID();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        var movements = user.getInventory().equipCosmetic(input.cosmeticId());

        userRepository.save(user);

        AuditEventFactory.inventoryChanges(
                movements,
                input.userId(),
                new AuditActor(AuditActorType.USER, input.userId(), null),
                null,
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);

        return buildOutput(user);
    }

    private ChangeCosmeticOutput buildOutput(User user) {
        return new ChangeCosmeticOutput(
            user
        );
    }
}
