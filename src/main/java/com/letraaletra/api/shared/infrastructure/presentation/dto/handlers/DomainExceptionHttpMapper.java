package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.exception.BadGatewayDomainException;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;
import com.letraaletra.api.shared.domain.exception.PayloadTooLargeDomainException;
import com.letraaletra.api.shared.domain.exception.TooManyRequestsDomainException;
import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DomainExceptionHttpMapper {

    private final Logger logger = LoggerFactory.getLogger(DomainExceptionHttpMapper.class);

    public HttpStatus resolve(DomainException ex) {
        if (ex instanceof NotFoundDomainException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof ForbiddenDomainException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof UnauthorizedDomainException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof ConflictDomainException) {
            return HttpStatus.CONFLICT;
        }
        if (ex instanceof PayloadTooLargeDomainException) {
            return HttpStatus.PAYLOAD_TOO_LARGE;
        }
        if (ex instanceof TooManyRequestsDomainException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        }
        if (ex instanceof BadGatewayDomainException) {
            return HttpStatus.BAD_GATEWAY;
        }
        if (ex instanceof BadRequestDomainException) {
            return HttpStatus.BAD_REQUEST;
        }

        logger.warn("DomainException without HTTP category mapped to 400: {}",
                ex.getClass().getName());

        return HttpStatus.BAD_REQUEST;
    }
}
