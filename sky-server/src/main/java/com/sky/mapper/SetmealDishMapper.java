package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
//这里新增的setmeal类，是为了获取套餐id，来看看是否删除菜品的ID是否还关联着套餐·
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);//根据菜品id查询套餐id

    /**
     * 批量插入套餐-菜品关联数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteBySetmealId(List<Long> ids);
@Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);



//    @Select("select sd.name, sd.copies, d.image, d.description " +
//            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
//            "where sd.setmeal_id = #{setmealId}")
//    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

}
