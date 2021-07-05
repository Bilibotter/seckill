package com.jwt.seckill.util;

public class IdUtil {
    private static final long MASK = (1<<13)-1;
    public static Long getId(Long i) {
        return (System.currentTimeMillis()<<13) + (i & MASK);
    }
}
