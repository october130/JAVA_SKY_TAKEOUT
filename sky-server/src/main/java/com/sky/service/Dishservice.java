package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;

public interface Dishservice {
    public void saveWithFlavor(DishDTO dishDTO);
}
