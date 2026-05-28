package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.Dishservice;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private Dishservice dishservice;
@Autowired
    private RedisTemplate redisTemplate;



    @PostMapping
    @ApiOperation("新增菜品")
    public Result save( @RequestBody DishDTO dishDTO) {
        //定义save方法,用来处理新增菜品的功能
        //当然，在新增菜品中图像上传的功能已经在common包中已经实现，所以这里就不在重复实现了
        log.info("新增菜品 :{}", dishDTO);
       dishservice.saveWithFlavor(dishDTO);//dishDTO对象中包含了菜品数据，以及菜品对应的口味数据

        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }

@GetMapping("/page")
@ApiOperation("菜品分页查询操作")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询返回的实体类：{}", dishPageQueryDTO);
      PageResult pageResult = dishservice.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("删除菜品")//批量删除菜品
    public Result delete(@RequestParam List<Long>ids){
        log.info("删除菜品：{}", ids);
        dishservice.delete(ids);//根据前端放的id进行批量删除

        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();

    }


    @GetMapping("/{id}")//查询回显，和员工操作一样，点击修改按钮时，可以看到该员工信息，然后再修改
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishservice.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
@PutMapping
@ApiOperation("修改菜品")
public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishservice.updateflavor(dishDTO);

    Set keys = redisTemplate.keys("dish_*");
    redisTemplate.delete(keys);
        return Result.success();

}


@PostMapping("/status/{status}")
    @ApiOperation("起售、停售菜品")
    public Result startORend(@PathVariable Integer status,Long id){
        log.info("起售、停售菜品：{},{}", status, id);
        dishservice.StartOrEnd(status,id);
        return Result.success();

    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品列表")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list = dishservice.listByCategoryId(categoryId);
        return Result.success(list);
    }

}
