package com.jwt.seckill.entity;

import java.io.Serializable;
import java.util.Date;

public class PromoCO implements Serializable {

    private static final long serialVersionUID = 8585462051025013194L;
    private Long id;

    private Double price;

    private Integer limit;

    public PromoCO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
