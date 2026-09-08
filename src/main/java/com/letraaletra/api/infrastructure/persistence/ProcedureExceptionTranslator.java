package com.letraaletra.api.infrastructure.persistence;

import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.offers.domain.exception.OfferAlreadyPurchasedException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.user.domain.ban.exception.UserAlreadyWasBannedException;
import com.letraaletra.api.features.user.domain.ban.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserCannotChangeNicknameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.exception.InvalidUserCosmeticSelectedException;
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
                    case "P0007" -> new CosmeticNotFoundException();
                    case "P0008" -> new InvalidCosmeticException();
                    case "P0009" -> new InvalidUserCosmeticSelectedException();
                    case "P0010" -> new UserAlreadyWasBannedException();
                    case "P0011" -> new UserDoesNotHaveBanException();
                    case "P0012" -> new UserCannotChangeNicknameException();
                    case "P0013" -> new NicknameAlreadyInUseException();
                    case "P0014" -> new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
                    default -> ex;
                };
            }
            // Postgres unique_violation 23505 mapped via message
            if ("23505".equals(state)) {
                if (msg != null && msg.contains("username")) return new NicknameAlreadyInUseException();
                if (msg != null && msg.contains("email")) return new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
                if (msg != null && msg.contains("user_inventory")) return new InvalidCosmeticException();
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
            if (msg.contains("CosmeticNotFound")) return new CosmeticNotFoundException();
            if (msg.contains("InvalidCosmetic")) return new InvalidCosmeticException();
            if (msg.contains("InvalidUserCosmeticSelected")) return new InvalidUserCosmeticSelectedException();
            if (msg.contains("UserAlreadyWasBanned")) return new UserAlreadyWasBannedException();
            if (msg.contains("UserDoesNotHaveBan")) return new UserDoesNotHaveBanException();
            if (msg.contains("UserCannotChangeNickname")) return new UserCannotChangeNicknameException();
            if (msg.contains("NicknameAlreadyInUse")) return new NicknameAlreadyInUseException();
            if (msg.contains("EmailAlreadyInUse")) return new com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException();
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
            if (msg.contains("CosmeticNotFound")) return new CosmeticNotFoundException();
            if (msg.contains("InvalidCosmetic")) return new InvalidCosmeticException();
            if (msg.contains("InvalidUserCosmeticSelected")) return new InvalidUserCosmeticSelectedException();
            if (msg.contains("UserAlreadyWasBanned")) return new UserAlreadyWasBannedException();
            if (msg.contains("UserDoesNotHaveBan")) return new UserDoesNotHaveBanException();
            if (msg.contains("UserCannotChangeNickname")) return new UserCannotChangeNicknameException();
            if (msg.contains("NicknameAlreadyInUse")) return new NicknameAlreadyInUseException();
        }
        if (ex instanceof RuntimeException re) return re;
        return new RuntimeException(ex);
    }
}
