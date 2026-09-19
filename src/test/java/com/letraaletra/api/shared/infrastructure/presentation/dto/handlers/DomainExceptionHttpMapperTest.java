package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.domain.exception.BadGatewayDomainException;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.shared.domain.exception.ForbiddenDomainException;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;
import com.letraaletra.api.shared.domain.exception.PayloadTooLargeDomainException;
import com.letraaletra.api.shared.domain.exception.TooManyRequestsDomainException;
import com.letraaletra.api.shared.domain.exception.UnauthorizedDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DomainExceptionHttpMapper - mapeamento de categoria para HTTP status")
class DomainExceptionHttpMapperTest {

    private enum TestMessages implements MessageCode {
        TEST_ERROR;

        @Override
        public String getCode() {
            return name();
        }

        @Override
        public String getMessage() {
            return "test error";
        }
    }

    private static class TestBadRequestException extends BadRequestDomainException {
        TestBadRequestException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestUnauthorizedException extends UnauthorizedDomainException {
        TestUnauthorizedException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestForbiddenException extends ForbiddenDomainException {
        TestForbiddenException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestNotFoundException extends NotFoundDomainException {
        TestNotFoundException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestConflictException extends ConflictDomainException {
        TestConflictException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestPayloadTooLargeException extends PayloadTooLargeDomainException {
        TestPayloadTooLargeException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestTooManyRequestsException extends TooManyRequestsDomainException {
        TestTooManyRequestsException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private static class TestBadGatewayException extends BadGatewayDomainException {
        TestBadGatewayException() {
            super(TestMessages.TEST_ERROR);
        }
    }

    private DomainExceptionHttpMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DomainExceptionHttpMapper();
    }

    @Test
    @DisplayName("BadRequestDomainException resolve 400")
    void shouldResolveBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, mapper.resolve(new TestBadRequestException()));
    }

    @Test
    @DisplayName("UnauthorizedDomainException resolve 401")
    void shouldResolveUnauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED, mapper.resolve(new TestUnauthorizedException()));
    }

    @Test
    @DisplayName("ForbiddenDomainException resolve 403")
    void shouldResolveForbidden() {
        assertEquals(HttpStatus.FORBIDDEN, mapper.resolve(new TestForbiddenException()));
    }

    @Test
    @DisplayName("NotFoundDomainException resolve 404")
    void shouldResolveNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, mapper.resolve(new TestNotFoundException()));
    }

    @Test
    @DisplayName("ConflictDomainException resolve 409")
    void shouldResolveConflict() {
        assertEquals(HttpStatus.CONFLICT, mapper.resolve(new TestConflictException()));
    }

    @Test
    @DisplayName("PayloadTooLargeDomainException resolve 413")
    void shouldResolvePayloadTooLarge() {
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, mapper.resolve(new TestPayloadTooLargeException()));
    }

    @Test
    @DisplayName("TooManyRequestsDomainException resolve 429")
    void shouldResolveTooManyRequests() {
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, mapper.resolve(new TestTooManyRequestsException()));
    }

    @Test
    @DisplayName("BadGatewayDomainException resolve 502")
    void shouldResolveBadGateway() {
        assertEquals(HttpStatus.BAD_GATEWAY, mapper.resolve(new TestBadGatewayException()));
    }

    @Test
    @DisplayName("exceção concreta desconhecida herda o status da categoria sem o mapper conhecê-la")
    void shouldResolveSubclassByCategoryWithoutKnowingIt() {
        class FutureCosmeticNotFoundException extends NotFoundDomainException {
            FutureCosmeticNotFoundException() {
                super(TestMessages.TEST_ERROR);
            }
        }

        assertEquals(HttpStatus.NOT_FOUND, mapper.resolve(new FutureCosmeticNotFoundException()));
    }

    @Test
    @DisplayName("DomainException sem categoria resolve 400 como fallback seguro")
    void shouldFallbackToBadRequest() {
        DomainException plain = new DomainException(TestMessages.TEST_ERROR);

        assertEquals(HttpStatus.BAD_REQUEST, mapper.resolve(plain));
    }
}
