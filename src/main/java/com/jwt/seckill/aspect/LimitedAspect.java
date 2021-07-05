package com.jwt.seckill.aspect;

import com.jwt.seckill.annotation.Limited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
public class LimitedAspect {
    @Qualifier(value = "limit")
    @Autowired
    private DefaultRedisScript<Long> limitScript;

    @Autowired
    private StringRedisTemplate template;

    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(com.jwt.seckill.annotation.Limited)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        Limited limited = method.getAnnotation(Limited.class);
        String spELString = limited.name();
        StringBuffer sb = new StringBuffer();
        if (spELString.startsWith("#")) {
            // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
            String[] paramNames = nameDiscoverer.getParameterNames(method);
            // 解析过后的Spring表达式对象
            Expression expression = parser.parseExpression(spELString);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 通过joinPoint获取被注解方法的形参
            Object[] args = point.getArgs();
            // 给上下文赋值
            for(int i = 0 ; i < args.length ; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            sb.append(expression.getValue(context).toString());
        }
        else {
            sb.append(spELString);
        }
        sb.append("::");
        sb.append(System.currentTimeMillis()/1000);
        List<String> cacheKeys = Collections.singletonList(sb.toString());
        String limitNum = String.valueOf(limited.limitNum());
        if (template.execute(limitScript, cacheKeys, limitNum.toString()) > 0) {
            return point.proceed();
        }
        else {
            throw new RuntimeException("服务器繁忙，请稍后再试！");
        }
    }
}
