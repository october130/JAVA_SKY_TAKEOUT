package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingAddService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
@Slf4j
public class ShoppingAddController {
@Autowired
    private ShoppingAddService shoppingAddService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")//将用户买的菜或者加入购物车
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingAddService.addShoppingCart( shoppingCartDTO);
        log.info("添加购物车：{}", shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
         List<ShoppingCart> list = shoppingAddService.list();
         log.info("查看购物车");
         return Result.success(list);

    }
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
public  Result remove(){
        log.info("清空购物车");
        shoppingAddService.clean();
        return Result.success();
}
@PostMapping("/sub")
@ApiOperation("购物车减操作")
public  Result subtract(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车减操作");
        shoppingAddService.subtract(shoppingCartDTO);
        return Result.success();
}

}
