package com.jwt.seckill.controller;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.entity.UserMsg;
import com.jwt.seckill.kafka.KafkaCallbackTemplate;
import com.jwt.seckill.kafka.KafkaTopics;
import com.jwt.seckill.oauth2.Oauth2UserDetails;
import com.jwt.seckill.payment.PaymentServiceWrapper;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.redis.TokenStatus;
import com.jwt.seckill.service.impl.CacheService;
import com.jwt.seckill.service.impl.StockServiceImpl;
import com.jwt.seckill.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/seckill/")
public class SeckillController {
    @Autowired
    private Random random;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private KafkaCallbackTemplate kafkaTemplate;

    @Autowired
    private StringRedisTemplate stringTemplate;

    @Autowired
    private RedisTemplate<Object, Object> objectTemplate;

    @Autowired
    private StockServiceImpl service;

    @Autowired
    CacheService cacheService;

    @Autowired
    private PaymentServiceWrapper payService;

    private final Logger logger = LoggerFactory.getLogger(SeckillController.class);


    @GetMapping("/stock/{stockId}")
    public String stocks(Model model, @PathVariable("stockId") Long stockId) {
        boolean start = objectTemplate.hasKey(CachePrefix.PROMO_PREFIX+stockId);
        if (!start) {
            return "unstarted";
        }
        Stock stock = service.getRecentStock(stockId);
        model.addAttribute("stock", stock);
        return "stocks";
    }

    // 校验验证码
    // 验证码正确则生成token
    // @Limited(name = "#stockId", limitNum = 200)
    @PostMapping(value = "/{stockId}/verify", produces = "text/plain;charset=UTF-8")
    public ModelAndView verify(HttpServletRequest request, Authentication authentication, @ModelAttribute UserMsg userMsg, @PathVariable("stockId") Long stockId) {
        String answer = (String) request.getSession().getAttribute("captcha");

        if (answer == null) {
            throw new RuntimeException("未获取验证码");
        }

        boolean correct = request.getParameter("verCode").equals(answer);

        if (correct) {
            userMsg.setUserId(getUserId(authentication));
            userMsg.setStockId(stockId);
            String token = CachePrefix.COUNT_TOKEN_HASH+userMsg.getUserId()+"::"+userMsg.getStockId();
            // request.getSession().setAttribute("token"+stockId, token);
            kafkaTemplate.send(KafkaTopics.VERIFY, userMsg);
            return new ModelAndView("redirect:/seckill/"+stockId+"/qualify");
        }
        request.getSession().removeAttribute("captcha");
        return new ModelAndView("redirect:/seckill/stock/"+stockId, Collections.singletonMap("answer", "error"));
    }

    @ResponseBody
    @GetMapping(value = "/{stockId}/qualify", produces = "text/plain;charset=UTF-8")
    public Object qualify(Authentication authentication, @PathVariable("stockId") Long stockId) {
        String token = CachePrefix.COUNT_TOKEN_HASH+getUserId(authentication)+"::"+stockId;
        if (token == null) {
            return "未获得秒杀资格";
        }
        String count = stringTemplate.opsForValue().get(token);
        if (count == null) {
            return "排队中请耐心等待";
        }
        if (count.equals(TokenStatus.FINISHED)){
            return "活动已结束";
        }
        if (count.equals(TokenStatus.INVALID_AMOUNT)) {
            return "超过限购";
        }
        if (count.equals("5")) {
            return "进入黑名单";
        }
        return new RedirectView("/seckill/"+stockId+"/pay");
    }

    @ResponseBody
    @GetMapping(value = "/{stockId}/pay", produces = "text/plain;charset=UTF-8")
    public String pay(Authentication authentication, @PathVariable("stockId") Long stockId) throws Exception {
        String token = CachePrefix.COUNT_TOKEN_HASH+getUserId(authentication)+"::"+stockId;
        Order order = cacheService.getOrder(token);
        if (order == null) {
            throw new RuntimeException("订单不存在或已超时!");
        }
        Long userId = getUserId(authentication);
        // 删除订单
        cacheService.deleteOrder(token);
        // 删除token
        if (!cacheService.deleteToken(token)) {
            logger.warn("用户{}尝试使用同一token多次支付", userId);
            cacheService.addToBlackList(userId);
            logger.info("用户{}已进入黑名单", userId);
            return "亲，你已进入黑名单!";
        }
        if (cacheService.execSoldScript(order.getStockId(), order.getAmount()) < 0) {
            return "已售罄";
        }
        payService.pay(order);
        return "请5分钟内完成支付";
    }

    private Long getUserId(Authentication authentication) {
        return ((Oauth2UserDetails)authentication.getPrincipal()).getId();
    }
}
