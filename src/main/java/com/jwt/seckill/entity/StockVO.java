package com.jwt.seckill.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class StockVO {
    @NotNull
    @Min(value = 0)
    private Double price;

    private Long stockId;

    private Integer amount;

    public StockVO() {
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
