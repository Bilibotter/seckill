package com.jwt.seckill.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class JwtTokenUtils {

    //@Value("${jwt.issuers}")
    private String issuers = "yhm";
    //@Value("${jwt.prefix}")
    private String prefix = "Bearer";
    //@Value("${jwt.secret}")
    private String secret = "metaphysics";
    private final static AtomicReference<Key> secretKey = new AtomicReference<>();
    //@Value("${jwt.expire}")
    private Long expire = 3600L;
    //@Value("${jwt.expiration-remember}")
    private Long expirationRemember = 604800L;

    public String createToken(String username, boolean remember) {
        Long expiration = remember ? expirationRemember : expire;
        return Jwts.builder()
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .setIssuer(issuers)
                // 主题
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration*1000))
                .compact();
    }

    public Key getSecretKey() {
        for (;;) {
            Key key = secretKey.get();
            if (key != null) {
                return key;
            }
            try {
                key = Keys.hmacShaKeyFor(MessageDigest.getInstance("SHA-512").digest(secret.getBytes()));
                if (secretKey.compareAndSet(null, key)) {
                    return key;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

    }

    public String getUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public boolean isExpired(String token) {
        return getTokenBody(token).getExpiration().before(new Date());
    }

    public Claims getTokenBody(String token) {
        // jws适用于Token验证
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
    }

    public String getPrefix() {
        return prefix;
    }
}
