package com.jwt.seckill.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (Promo)实体类
 *
 * @author makejava
 * @since 2021-06-28 16:43:45
 */
public class Promo implements Serializable {
    private static final long serialVersionUID = 456390008415679993L;

    private Long id;

    private String name;

    private Date start;

    private Date end;

    private Long stockId;

    private Double price;

    private Object finish;

    private Integer limit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Object getFinish() {
        return finish;
    }

    public void setFinish(Object finish) {
        this.finish = finish;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Promo{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", stockId=").append(stockId);
        sb.append(", price=").append(price);
        sb.append(", finish=").append(finish);
        sb.append(", limit=").append(limit);
        sb.append('}');
        return sb.toString();
    }
}
