package com.jwt.seckill.entity;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (Stock)实体类
 *
 * @author makejava
 * @since 2021-06-20 15:23:15
 */
public class Stock implements Serializable {
    private static final long serialVersionUID = -27009192270025114L;

    private Long id;

    @Length(max = 40)
    private String title;

    @NotNull
    @Min(value = 0)
    private Double price;

    @NotNull
    @Min(value = 0)
    private Object remain;

    @NotNull
    @Min(value = 0)
    private Object sold;

    @Length(max = 255)
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Object getRemain() {
        return remain;
    }

    public void setRemain(Object remain) {
        this.remain = remain;
    }

    public Object getSold() {
        return sold;
    }

    public void setSold(Object sold) {
        this.sold = sold;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Stock{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", price=").append(price);
        sb.append(", remain=").append(remain);
        sb.append(", sold=").append(sold);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
