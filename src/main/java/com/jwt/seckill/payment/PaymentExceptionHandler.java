package com.jwt.seckill.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ControllerAdvice
public class PaymentExceptionHandler {
    private final String pattern = "Order {0} {1}";
    @ExceptionHandler(PaymentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public String handlerPayTimeOut(PaymentException e) {
        return MessageFormat.format(pattern, e.getData(), e.getMessage());
    }
}
