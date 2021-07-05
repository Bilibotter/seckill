package com.jwt.seckill.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Random;

@Configuration
public class LuaScriptConfig {
    @Primary
    @Bean(name = "limit")
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
        script.setResultType(Long.class);
        return script;
    }

    @Bean(name = "sold")
    public DefaultRedisScript<Long> soldScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("sold.lua")));
        script.setResultType(Long.class);
        return script;
    }
}
