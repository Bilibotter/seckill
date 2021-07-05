package com.jwt.seckill.entity;

import java.io.Serializable;

public class UserMsg implements Serializable {
    private static final long serialVersionUID = 3978226123871540175L;
    private Long userId;
    private Long stockId;
    private Integer amount;

    public UserMsg() {
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserMsg{");
        sb.append("userId=").append(userId);
        sb.append(", stockId=").append(stockId);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
