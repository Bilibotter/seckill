package com.jwt.seckill.controller;

import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (Stock)表控制层
 *
 * @author makejava
 * @since 2021-06-20 15:23:16
 */
@Controller
@RequestMapping("stock")
public class StockController {
    /**
     * 服务对象
     */
    @Resource
    private StockService stockService;

    @GetMapping("/test")
    public String test() {
        return "stock";
    }

    @ResponseBody
    @PostMapping("/create")
    public String createStock(@Valid @ModelAttribute Stock stock) {
        return stockService.insert(stock).toString();
    }
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ResponseBody
    @GetMapping("/get/{id}")
    public Stock selectOne(@PathVariable("id") Long id) {
        return this.stockService.queryById(id);
    }

}
