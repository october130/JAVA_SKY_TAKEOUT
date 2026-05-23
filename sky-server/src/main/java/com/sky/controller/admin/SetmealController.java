package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
     private SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("套餐分页查询操作")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询：{}，{}, {}", setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize(), setmealPageQueryDTO.getName());
         PageResult pageResult = setmealService.page(setmealPageQueryDTO);
         log.info("分页查询结果：{}", pageResult);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增套餐操作")
    public Result save( @RequestBody SetmealDTO setmealDTO ){
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

}
