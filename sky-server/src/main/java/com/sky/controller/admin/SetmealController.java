package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping
    @ApiOperation("批量删除套餐操作")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐：{}", ids);
        setmealService.delete(ids);
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }


    @PutMapping
    @ApiOperation("修改套餐操作")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }



    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售操作")
    public Result startORend(@PathVariable Integer status, long id){
        log.info("套餐起售、停售：{}", status);
        setmealService.startORend(status, id);
        return Result.success();
    }
}
