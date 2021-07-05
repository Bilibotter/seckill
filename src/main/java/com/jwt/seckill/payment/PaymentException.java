package com.jwt.seckill.payment;

public class PaymentException extends RuntimeException {
    private Object data;

    public PaymentException() {
        super();
    }

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(Throwable cause) {
        super(cause);
    }

    public void setData(Object o) {
        data = o;
    }
    public Object getData() {
        return data;
    }
}
