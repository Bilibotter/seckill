package com.jwt.seckill.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Random;

@Configuration
public class PromoUtil {
    @Bean
    Random random() {
        return new Random();
    }

    @Bean("StdDateFormat")
    SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
