package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.Dishservice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl  implements Dishservice {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {



        Dish dish = new Dish();//创建dish对象
        BeanUtils.copyProperties(dishDTO, dish);//为什么要将dishDTO中的数据复制给dish对象呢？
        // 原因是：dishDTO中的数据是前端页面传过来的，dish对象中的数据是数据库中的数据，所以要复制

        dishMapper.insert(dish);//插入菜品数据,这个只用插入一条

        Long id = dish.getId();//获取插入的菜品的id


        List<DishFlavor> flavors = dishDTO.getFlavors();//获取菜品口味数据,将菜品口味数据封装集合
        // ，因为只有dishdto中有菜品口味数据，所以用dishdto来获取
        if (flavors != null && flavors.size() > 0) {

            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);//将菜品id设置给菜品口味对象
            }
           dishFlavorMapper.insertBatch(flavors);//批量插入菜品口味数据
        }

    }
}
