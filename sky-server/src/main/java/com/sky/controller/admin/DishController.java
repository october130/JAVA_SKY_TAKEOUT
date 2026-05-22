package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.Result;
import com.sky.service.Dishservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private Dishservice dishservice;


    @PostMapping
    @ApiOperation("新增菜品")
    public Result save( @RequestBody DishDTO dishDTO) {
        //定义save方法,用来处理新增菜品的功能
        //当然，在新增菜品中图像上传的功能已经在common包中已经实现，所以这里就不在重复实现了
        log.info("新增菜品 :{}", dishDTO);
       dishservice.saveWithFlavor(dishDTO);//dishDTO对象中包含了菜品数据，以及菜品对应的口味数据

        return Result.success();
    }

}
