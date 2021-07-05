package com.jwt.seckill.entity;

import java.io.Serializable;

public class StockMsg implements Serializable {

    private static final long serialVersionUID = -7419883706519784543L;
    private Long id;
    private Integer amount;
    private Long OrderId;

    public Long getOrderId() {
        return OrderId;
    }

    public void setOrderId(Long orderId) {
        OrderId = orderId;
    }

    public StockMsg() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StockMsg{");
        sb.append("id=").append(id);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
