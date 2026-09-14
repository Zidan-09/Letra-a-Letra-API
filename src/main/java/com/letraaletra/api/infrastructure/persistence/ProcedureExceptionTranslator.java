package com.letraaletra.api.infrastructure.persistence;

import com.letraaletra.api.features.inventory.domain.exception.DuplicateUniqueItemException;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.MaxStackExceededException;
import com.letraaletra.api.features.inventory.domain.exception.NonConsumableItemException;
import com.letraaletra.api.features.inventory.domain.exception.NonEquipableItemException;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.offers.domain.exception.OfferAlreadyPurchasedException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.user.domain.ban.exception.UserAlreadyWasBannedException;
import com.letraaletra.api.features.user.domain.ban.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserCannotChangeNicknameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.wallet.exception.InsufficientBalanceException;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

public final class ProcedureExceptionTranslator {

    private ProcedureExceptionTranslator() {}

    public static RuntimeException translate(DataAccessException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SQLException sql) {
            String state = sql.getSQLState();
            String msg = sql.getMessage();
            if (state != null) {
                return switch (state) {
                    case "P0001" -> {
                        if (msg != null && msg.contains("OfferNotFound")) yield new OfferNotFoundException();
                        if (msg != null && msg.contains("UserNotFound")) yield new UserNotFoundException();
                        yield new OfferNotFoundException();
                    }
                    case "P0002" -> new InvalidOfferStatusException();
                    case "P0003" -> new InvalidPaymentException();
                    case "P0004" -> new OfferAlreadyPurchasedException();
                    case "P0005" -> new UserNotFoundException();
                    case "P0006" -> new InsufficientBalanceException();
                    case "P0010" -> new UserAlreadyWasBannedException();
                    case "P0011" -> new UserDoesNotHaveBanException();
                    case "P0012" -> new UserCannotChangeNicknameException();
                    case "P0013" -> new NicknameAlreadyInUseException();
                    case "P0014" -> new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
                    case "P0015" -> new ItemNotFoundException();
                    case "P0016" -> new DuplicateUniqueItemException();
                    case "P0017" -> new ItemNotAvailableException();
                    case "P0018" -> new MaxStackExceededException();
                    case "P0019" -> new InvalidQuantityException();
                    case "P0020" -> new NonConsumableItemException();
                    case "P0021" -> new NonEquipableItemException();
                    case "P0022" -> new InsufficientQuantityException();
                    case "P0023" -> new InapplicableContextException();
                    case "P0024" -> new ItemNotOwnedException();
                    default -> ex;
                };
            }
            // Postgres unique_violation 23505 mapped via message
            if ("23505".equals(state)) {
                if (msg != null && msg.contains("username")) return new NicknameAlreadyInUseException();
                if (msg != null && msg.contains("email")) return new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
            }
            if ("23514".equals(state)) {
                return new InsufficientBalanceException();
            }
        }
        // Spring wraps PSQLException in UncategorizedSQLException with sqlState in cause
        String msg = ex.getMessage();
        if (msg != null) {
            if (msg.contains("OfferNotFound")) return new OfferNotFoundException();
            if (msg.contains("InvalidOfferStatus")) return new InvalidOfferStatusException();
            if (msg.contains("InvalidPayment")) return new InvalidPaymentException();
            if (msg.contains("OfferAlreadyPurchased")) return new OfferAlreadyPurchasedException();
            if (msg.contains("UserNotFound")) return new UserNotFoundException();
            if (msg.contains("InsufficientBalance")) return new InsufficientBalanceException();
            if (msg.contains("UserAlreadyWasBanned")) return new UserAlreadyWasBannedException();
            if (msg.contains("UserDoesNotHaveBan")) return new UserDoesNotHaveBanException();
            if (msg.contains("UserCannotChangeNickname")) return new UserCannotChangeNicknameException();
            if (msg.contains("NicknameAlreadyInUse")) return new NicknameAlreadyInUseException();
            if (msg.contains("EmailAlreadyInUse")) return new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
            if (msg.contains("ItemNotFound")) return new ItemNotFoundException();
            if (msg.contains("DuplicateUniqueItem")) return new DuplicateUniqueItemException();
            if (msg.contains("ItemNotAvailable")) return new ItemNotAvailableException();
            if (msg.contains("MaxStackExceeded")) return new MaxStackExceededException();
            if (msg.contains("InvalidQuantity")) return new InvalidQuantityException();
            if (msg.contains("NonConsumableItem")) return new NonConsumableItemException();
            if (msg.contains("NonEquipableItem")) return new NonEquipableItemException();
            if (msg.contains("InsufficientQuantity")) return new InsufficientQuantityException();
            if (msg.contains("InapplicableContext")) return new InapplicableContextException();
            if (msg.contains("ItemNotOwned")) return new ItemNotOwnedException();
            if (msg.contains("InvalidToken") || msg.contains("P0000")) return new InvalidTokenException();
        }
        return ex;
    }

    public static RuntimeException translateGeneric(Exception ex) {
        if (ex instanceof DataAccessException dae) return translate(dae);
        String msg = ex.getMessage();
        if (msg != null) {
            if (msg.contains("OfferNotFound")) return new OfferNotFoundException();
            if (msg.contains("InvalidOfferStatus")) return new InvalidOfferStatusException();
            if (msg.contains("InvalidPayment")) return new InvalidPaymentException();
            if (msg.contains("OfferAlreadyPurchased")) return new OfferAlreadyPurchasedException();
            if (msg.contains("UserNotFound")) return new UserNotFoundException();
            if (msg.contains("InsufficientBalance")) return new InsufficientBalanceException();
            if (msg.contains("UserAlreadyWasBanned")) return new UserAlreadyWasBannedException();
            if (msg.contains("UserDoesNotHaveBan")) return new UserDoesNotHaveBanException();
            if (msg.contains("UserCannotChangeNickname")) return new UserCannotChangeNicknameException();
            if (msg.contains("NicknameAlreadyInUse")) return new NicknameAlreadyInUseException();
            if (msg.contains("ItemNotFound")) return new ItemNotFoundException();
            if (msg.contains("DuplicateUniqueItem")) return new DuplicateUniqueItemException();
            if (msg.contains("ItemNotAvailable")) return new ItemNotAvailableException();
            if (msg.contains("MaxStackExceeded")) return new MaxStackExceededException();
            if (msg.contains("InvalidQuantity")) return new InvalidQuantityException();
            if (msg.contains("NonConsumableItem")) return new NonConsumableItemException();
            if (msg.contains("NonEquipableItem")) return new NonEquipableItemException();
            if (msg.contains("InsufficientQuantity")) return new InsufficientQuantityException();
            if (msg.contains("InapplicableContext")) return new InapplicableContextException();
            if (msg.contains("ItemNotOwned")) return new ItemNotOwnedException();
        }
        if (ex instanceof RuntimeException re) return re;
        return new RuntimeException(ex);
    }
}
