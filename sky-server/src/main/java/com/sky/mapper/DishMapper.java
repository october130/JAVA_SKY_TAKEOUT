package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish  where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

@AutoFill( value = OperationType.INSERT)
    void insert(Dish dish);

    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);//批量删除

    //根据id查询相应的菜品数据
@Select("select * from sky_take_out.dish where id = #{id}")
    Dish getById(Long id);

//根据ID动态修改菜品数据，不包括菜品风味数据
@AutoFill( value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品列表（用于套餐新增时选择菜品）
     */
    @Select("select * from sky_take_out.dish where category_id = #{categoryId} and status = 1")
    List<Dish> listByCategoryId(Long categoryId);
}
