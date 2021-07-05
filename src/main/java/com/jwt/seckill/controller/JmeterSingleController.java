package com.jwt.seckill.controller;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.entity.UserMsg;
import com.jwt.seckill.kafka.KafkaCallbackTemplate;
import com.jwt.seckill.kafka.KafkaTopics;
import com.jwt.seckill.payment.PaymentServiceWrapper;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.redis.TokenStatus;
import com.jwt.seckill.service.impl.CacheService;
import com.jwt.seckill.service.impl.OrderServiceImpl;
import com.jwt.seckill.service.impl.StockServiceImpl;
import com.jwt.seckill.service.impl.UserServiceImpl;
import com.jwt.seckill.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/single/")
public class JmeterSingleController {
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
    OrderServiceImpl orderService;

    @Autowired
    CacheService cacheService;

    @Autowired
    private PaymentServiceWrapper payService;

    private static final AtomicLong usersId = new AtomicLong(1);

    private final Logger logger = LoggerFactory.getLogger(SeckillController.class);


    @ResponseBody
    @GetMapping(value = "/verifys", produces = "text/plain;charset=UTF-8")
    public String oversold() {
        UserMsg userMsg = new UserMsg();
        Long stockId = 8L+random.nextInt(10);
        Long userId = UUID.randomUUID().getMostSignificantBits();
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        kafkaTemplate.send(KafkaTopics.VERIFY, userMsg);
        logger.info("Send"+userMsg);
        return "success";
    }

    @ResponseBody
    @GetMapping(value = "/nosend", produces = "text/plain;charset=UTF-8")
    public String nosend() {
        UserMsg userMsg = new UserMsg();
        Long stockId = 8L+random.nextInt(10);
        Long userId = UUID.randomUUID().getMostSignificantBits();
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        return "success";
    }

    @GetMapping(value = "/verify", produces = "text/plain;charset=UTF-8")
    public Object verify() {
        UserMsg userMsg = new UserMsg();
        Long userId = usersId.incrementAndGet();
        Long stockId = 8L+userId%10;
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        // request.getSession().setAttribute("token"+stockId, token);
        kafkaTemplate.send(KafkaTopics.VERIFY, userMsg);
        return "success";
    }

    @GetMapping(value = "/verifyss", produces = "text/plain;charset=UTF-8")
    public Object verifys() {
        UserMsg userMsg = new UserMsg();
        Long userId = UUID.randomUUID().getMostSignificantBits();
        Long stockId = 8L+random.nextInt(10);
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
        String count = stringTemplate.opsForValue().get(CachePrefix.REMAIN_PREFIX+stockId);
        if (count == null) {
            if (cacheService.inBlackList(userId)) {
                return "已进入黑名单";
            }
        }
        if(count.equals(TokenStatus.SOLD_OUT)) {
            return "已售罄";
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
        return "success";
    }

    @ResponseBody
    @GetMapping("/all")
    public String single() {
        UserMsg userMsg = new UserMsg();
        Long userId = UUID.randomUUID().getMostSignificantBits();
        Long stockId = 8L+random.nextInt(10);
        userMsg.setUserId(userId);
        userMsg.setStockId(stockId);
        userMsg.setAmount(random.nextInt(3)+1);
        if (cacheService.inBlackList(userMsg.getUserId())) {
            return "fail";
        }
        PromoCO promoCO = cacheService.getPromoCO(userMsg.getStockId());

        String token = CachePrefix.COUNT_TOKEN_HASH+userMsg.getUserId()+"::"+userMsg.getStockId();;

        // 活动已结束
        if (promoCO == null) {
            cacheService.setTokenState(token, TokenStatus.FINISHED);
            return "fail";
        }
        // 超过限购
        else if (userMsg.getAmount() > promoCO.getLimit()) {
            cacheService.setTokenState(token, TokenStatus.INVALID_AMOUNT);
            return "fail";
        }
        // 已抢购过还未付款
        else if (stringTemplate.hasKey(token)) {
            Long count = cacheService.incrToken(token);
            // 进黑名单待一天
            if (count == 5) {
                cacheService.addToBlackList(userMsg.getUserId());
            }
            return "fail";
        }
        Order order = new Order();
        order.setUserId(userMsg.getUserId());
        order.setStockId(userMsg.getStockId());
        order.setAmount(userMsg.getAmount());
        order.setPromoId(promoCO.getId());
        order.setStockPrice(promoCO.getPrice());
        order.setTotalPrice(userMsg.getAmount()*promoCO.getPrice());
        order.setId(IdUtil.getId((long) order.hashCode()));
        cacheService.addOrder(order, token);
        // 最后加token，保证有token时一定有order
        cacheService.addToken(token);
        //orderService.createOrder(order);
        order = cacheService.getOrder(token);
        cacheService.deleteOrder(token);
        cacheService.deleteToken(token);
        return "success";
    }
}
