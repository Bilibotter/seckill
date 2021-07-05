package com.jwt.seckill.entity;

import java.io.Serializable;

/**
 * (Order)实体类
 *
 * @author makejava
 * @since 2021-06-26 12:44:29
 */
public class Order implements Serializable {
    private static final long serialVersionUID = -26896015422105326L;

    private Long id;

    private Long userId;

    private Long stockId;

    private Long promoId;

    private Integer amount;

    private Double stockPrice;

    private Double totalPrice;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPromoId() {
        return promoId;
    }

    public void setPromoId(Long promoId) {
        this.promoId = promoId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Order{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", stockId=").append(stockId);
        sb.append(", promoId=").append(promoId);
        sb.append(", amount=").append(amount);
        sb.append(", stockPrice=").append(stockPrice);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder().append(id, order.id).append(userId, order.userId).append(stockId, order.stockId).append(promoId, order.promoId).append(amount, order.amount).append(stockPrice, order.stockPrice).append(totalPrice, order.totalPrice).isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37).append(id).append(userId).append(stockId).append(promoId).append(amount).append(stockPrice).append(totalPrice).toHashCode();
    }
}
