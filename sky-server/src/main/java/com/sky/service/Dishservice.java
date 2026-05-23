package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface Dishservice {
    public void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    void updateflavor(DishDTO dishDTO);
}
