package com.jwt.seckill.entity;

import com.jwt.seckill.validator.Mobile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;

public class VerifyVO {
    @NotNull
    @Mobile
    private String tel;
    @NotNull
    private String code;
    private String password;
    private static final BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
    // $2a$10$mpc6L00a1HixaKxkwPy9Jux6fiw/fbGY6xHnKZqAIp3rOzbf.aEdG
    private static final String EXPECTED_PASSWORD = "I am smart front end!";
    public static final String CACHE_PREFIX = "verify::";

    public VerifyVO() {
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFrontEnd() {
        assert password != null;
        return encoder.matches(EXPECTED_PASSWORD, password);
    }
}
