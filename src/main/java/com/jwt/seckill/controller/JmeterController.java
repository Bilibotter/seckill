package com.jwt.seckill.controller;

import com.jwt.seckill.annotation.Limited;
import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.entity.UserMsg;
import com.jwt.seckill.kafka.KafkaCallbackTemplate;
import com.jwt.seckill.kafka.KafkaTopics;
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
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/jmeter/")
public class JmeterController {
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


    @GetMapping("/stock/{stockId}/{userId}")
    public String stocks(Model model,
                         @PathVariable("stockId") Long stockId,
                         @PathVariable("userId") Long userId) {
        boolean start = objectTemplate.hasKey(CachePrefix.PROMO_PREFIX+stockId);
        if (!start) {
            return "unstarted";
        }
        Stock stock = service.getRecentStock(stockId);
        model.addAttribute("stock", stock);
        return "stocks";
    }

    @GetMapping(value = "/verifys", produces = "text/plain;charset=UTF-8")
    public ModelAndView oversold() {
        UserMsg userMsg = new UserMsg();
        Long stockId = 18L;
        Long userId = UUID.randomUUID().getMostSignificantBits();
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        // request.getSession().setAttribute("token"+stockId, token);
        kafkaTemplate.send(KafkaTopics.VERIFY, userMsg);
        return new ModelAndView("redirect:/jmeter/qualify/"+stockId+"/"+userId);
    }

    // ???????????????
    // ????????????????????????token
    // @Limited(name = "limits", limitNum = 100)
    @GetMapping(value = "/verify", produces = "text/plain;charset=UTF-8")
    public ModelAndView verify() {
        UserMsg userMsg = new UserMsg();
        Long stockId = 8L+random.nextInt(10);
        Long userId = UUID.randomUUID().getMostSignificantBits();
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        // request.getSession().setAttribute("token"+stockId, token);
        kafkaTemplate.send(KafkaTopics.VERIFY, userMsg);
        return new ModelAndView("redirect:/jmeter/qualify/"+stockId+"/"+userId);
    }

    @ResponseBody
    @GetMapping(value = "/qualify/{stockId}/{userId}", produces = "text/plain;charset=UTF-8")
    public Object qualify(
                          @PathVariable("stockId") Long stockId,
                          @PathVariable("userId") Long userId) throws Exception {
        String token = CachePrefix.COUNT_TOKEN_HASH+userId+"::"+stockId;

        String count = stringTemplate.opsForValue().get(token);
        while (count == null) {
            if (cacheService.inBlackList(userId)) {
                return "??????????????????";
            }
            Thread.sleep(500);
            count = stringTemplate.opsForValue().get(token);
        }
        if (count.equals(TokenStatus.FINISHED)){
            return "???????????????";
        }
        if (count.equals(TokenStatus.INVALID_AMOUNT)) {
            return "????????????";
        }
        if (count.equals(TokenStatus.BUY_DUPLICATE)) {
            return "?????????????????????";
        }
        if (count.equals("5")) {
            return "???????????????";
        }
        return new RedirectView("/jmeter/pay/"+stockId+"/"+userId);
    }

    @ResponseBody
    @GetMapping(value = "/pay/{stockId}/{userId}", produces = "text/plain;charset=UTF-8")
    public String pay(
                      @PathVariable("stockId") Long stockId,
                      @PathVariable("userId") Long userId) throws Exception {
        String token = CachePrefix.COUNT_TOKEN_HASH+userId+"::"+stockId;;
        Order order = cacheService.getOrder(token);
        if (order == null) {
            throw new RuntimeException("???????????????????????????!");
        }
        // ????????????
        cacheService.deleteOrder(token);
        // ??????token
        if (!cacheService.deleteToken(token)) {
            logger.warn("??????{}??????????????????token????????????", userId);
            cacheService.addToBlackList(userId);
            logger.info("??????{}??????????????????", userId);
            return "???????????????????????????!";
        }
        if (cacheService.execSoldScript(order.getStockId(), order.getAmount()) < 0) {
            return "?????????";
        }
        payService.pay(order);
        return "???5?????????????????????";
    }

    private Long getUserId(HttpSession session) {
        return 1L;
    }
}
