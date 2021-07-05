package com.jwt.seckill.entity;

import com.jwt.seckill.validator.Mobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RegisterVO implements Serializable {
    @NotNull
    @Length(max = 20)
    private String name;
    /**
     * BCrypt
     */
    @NotNull
    @Length(max = 20)
    private String password;

    @Mobile
    @Length(max = 20)
    private String tel;
    // 验证码
    @NotNull
    @Length(max = 4)
    private String code;

    public RegisterVO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        User user = new User();
        user.setName(getName());
        user.setPassword(getPassword());
        user.setTel(getTel());
        return user;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RegisterVO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", tel='").append(tel).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
