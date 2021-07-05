package com.jwt.seckill.controller;

import com.jwt.seckill.dao.UserDao;
import com.jwt.seckill.entity.RegisterVO;
import com.jwt.seckill.entity.User;
import com.jwt.seckill.entity.VerifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Duration;

@Controller
@RequestMapping("/register")
public class RegisterController {
    //@Autowired
    private UserDao dao;

    //@Autowired
    private StringRedisTemplate template;

    //@Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    public RegisterController(UserDao dao, StringRedisTemplate template, BCryptPasswordEncoder encoder) {
        this.dao = dao;
        this.template = template;
        this.encoder = encoder;
    }

    @GetMapping
    public String registerPage() {
        return "register";
    }

    @ResponseBody
    @PostMapping
    public String register(HttpServletRequest request, @ModelAttribute @Valid RegisterVO registerVO) {
        String key = VerifyVO.CACHE_PREFIX+registerVO.getTel();
        String code = registerVO.getCode();
        if (code == null || !CaptchaUtil.ver(code, request)) {
            throw new VerifyError("验证码错误");
        }
        CaptchaUtil.clear(request);
        // 使用后删除验证码
        template.delete(key);
        User user = registerVO.getUser();
        // 给密码加密后再存入
        user.setPassword(encoder.encode(user.getPassword()));
        String res = dao.insert(user) == 1 ? " success" : " fail";
        return "Create "+user+res+" !";
    }

    @ResponseBody
    @PostMapping("/verify")
    public String verification(@ModelAttribute @Valid VerifyVO verifyVO) {
        if (!verifyVO.isFrontEnd()) {
            throw new RuntimeException("验证密码错误");
        }
        template.opsForValue().set(VerifyVO.CACHE_PREFIX+verifyVO.getTel(), verifyVO.getCode(), Duration.ofSeconds(120));
        return "200";
    }
}
