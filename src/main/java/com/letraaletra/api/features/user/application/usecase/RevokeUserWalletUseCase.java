package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.RevokeUserWalletInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.UUID;

public class RevokeUserWalletUseCase implements UseCase<RevokeUserWalletInput, Void> {
    private static final String SOURCE_DETAIL = "REVOKE_USER_WALLET";

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminChecker adminChecker;
    private final BusinessAuditRecorder auditRecorder;

    public RevokeUserWalletUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.adminChecker = adminChecker;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public Void execute(RevokeUserWalletInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        UUID operationId = UUID.randomUUID();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        WalletMovement movement = user.getWallet().remove(input.type(), input.amount());

        userRepository.save(user);

        Transaction saved = transactionRepository.save(Transaction.create(
                input.userId(),
                input.type(),
                input.amount(),
                movement.balanceBefore()
                        .getAmountFor(input.type()),
                movement.balanceAfter()
                        .getAmountFor(input.type()),
                OperationType.DEBIT,
                TransactionReason.ADMIN_REVOKE,
                input.principal().auth()
        ));

        auditRecorder.record(AuditEventFactory.walletMovement(
                movement,
                input.userId(),
                new AuditActor(AuditActorType.ADMIN, input.principal().auth(), input.principal().name()),
                TransactionReason.ADMIN_REVOKE.name(),
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId,
                saved.transactionId()
        ));

        return null;
    }
}
