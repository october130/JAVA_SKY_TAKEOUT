package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺营业状态相关接口")
public class ShoppingController {
    public  static final String key  = "店铺营业状态";
    @Autowired
    private RedisTemplate redisTemplate;

@PutMapping("/{status}")
@ApiOperation("商家设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(  key,status);
        return  Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("商家查询营业状态")
    public Result<Integer> getStatus() {
        log.info("商家查询营业状态");
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get( key);
        log.info("当前营业状态为：{}", shopStatus != null && shopStatus == 1 ? "营业中" : "打烊中");
        return  Result.success(shopStatus);
    }

}
