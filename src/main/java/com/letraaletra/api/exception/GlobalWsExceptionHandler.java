package com.letraaletra.api.exception;

import com.letraaletra.api.dto.response.ErrorResponse;
import com.letraaletra.api.exception.messages.ServerMessages;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class GlobalWsExceptionHandler {

    @MessageExceptionHandler(AppException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleAppException(AppException ex) {
        return new ErrorResponse(false, ex.getMessage());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse(false, ServerMessages.INTERNAL_ERROR.getMessage());
    }
}
