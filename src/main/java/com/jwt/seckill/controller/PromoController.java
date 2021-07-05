package com.jwt.seckill.controller;

import com.jwt.seckill.entity.Promo;
import com.jwt.seckill.service.PromoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Promo)表控制层
 *
 * @author makejava
 * @since 2021-06-17 18:29:15
 */
@RestController
@RequestMapping("promo")
public class PromoController {
    /**
     * 服务对象
     */
    @Resource
    private PromoService promoService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Promo selectOne(Long id) {
        return this.promoService.queryById(id);
    }

}
