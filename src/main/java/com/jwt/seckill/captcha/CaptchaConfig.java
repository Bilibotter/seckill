package com.jwt.seckill.captcha;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.io.IOException;

@Configuration
public class CaptchaConfig {
    @Bean
    public ArithmeticCaptcha arithmeticCaptcha() throws IOException, FontFormatException {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha();
        captcha.setFont(Captcha.FONT_10);
        captcha.setWidth(260);
        captcha.setHeight(96);
        return captcha;
    }
}
