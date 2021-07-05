package com.jwt.seckill.exception;

public interface CommonException {
    Integer getCode();
    String getMsg();
    String setMsg(String msg);
}
