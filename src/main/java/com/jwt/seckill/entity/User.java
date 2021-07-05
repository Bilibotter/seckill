package com.jwt.seckill.entity;

import com.jwt.seckill.validator.Mobile;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2021-06-17 18:29:16
 */
@Valid
public class User implements Serializable {
    private static final long serialVersionUID = -65563697232013188L;
    /**
     * 用户ID
     */
    private Long id;

    private String name;
    /**
     * BCrypt
     */
    private String password;

    @Mobile
    private String tel;

    private String head;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", tel='").append(tel).append('\'');
        sb.append(", head='").append(head).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
