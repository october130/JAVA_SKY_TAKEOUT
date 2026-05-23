package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> Flavor);

    void deleteByDishId(List<Long> dishids);//这个是批量删除，后面的修改风味时先删除全部的以前设定的风味
    //就要用到这个方法


@Select( "select * from sky_take_out.dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
