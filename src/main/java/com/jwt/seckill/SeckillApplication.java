package com.jwt.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableRetry
@EnableKafka
@EnableScheduling
@EnableAsync
@EnableWebMvc
// @EnableCaching
@MapperScan(value = {"com.jwt.seckill.dao"})
@SpringBootApplication
public class SeckillApplication {

	public static void main(String[] args) {

		SpringApplication.run(SeckillApplication.class, args);
	}

}
