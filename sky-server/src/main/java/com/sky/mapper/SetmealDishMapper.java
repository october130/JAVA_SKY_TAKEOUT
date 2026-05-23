package com.sky.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
//这里新增的setmeal类，是为了获取套餐id，来看看是否删除菜品的ID是否还关联着套餐·
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);//根据菜品id查询套餐id
}
