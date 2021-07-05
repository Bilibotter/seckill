package com.jwt.seckill.controller;

import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.service.impl.UserServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@Controller
public class CaptchaController {
    @Autowired
    private UserServiceImpl service;
    @Autowired
    private ArithmeticCaptcha captcha;

    @Autowired
    private StringRedisTemplate template;

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CaptchaUtil.out(request, response);
    }

    @GetMapping("/captcha/seckill")
    public void countCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String key = CachePrefix.VERIFY_COUNT+getUserId(request.getSession());
        Long count = template.opsForValue().increment(key);
        // 获取验证码超过6次则10分钟后才可以重新获取
        if (count < 6L) {
            request.getSession().setAttribute("captcha", captcha.text());
            captcha.out(response.getOutputStream());
            template.expire(key, 1, TimeUnit.MINUTES);
        }
        else {
            if (count == 6L) {
                template.expire(key, 10, TimeUnit.MINUTES);
            }
            getTooFreq(response);
        }
    }

    private void getTooFreq(HttpServletResponse response) throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/static/captcha.png");
        ByteBuffer buffer = ByteBuffer.allocate(in.available());
        while (in.available() > 0) {
            buffer.put((byte) in.read());
        }
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(buffer.array());
        outputStream.flush();
        outputStream.close();
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userId = service.getIdByTel(authentication.getName()).getId();
            session.setAttribute("userId", userId);
        }
        return userId;
    }
}
