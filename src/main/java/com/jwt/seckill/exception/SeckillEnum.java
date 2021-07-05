package com.jwt.seckill.exception;

public enum SeckillEnum implements CommonException {
    VALIDATE_EXCEPTION(100001, "参数错误"),
    UNKNOWN_EXCEPTION(100002, "未知错误"),
    USER_NOT_EXIST(200001, "用户不存在"),
    UNLOGIN_EXCEPTION(200002, "登录才可访问"),
    STOCK_NOT_ENOUGH(300001, "库存不足");
    private Integer code;
    private String msg;

    SeckillEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public String setMsg(String msg) {
        return this.msg = msg;
    }
}
