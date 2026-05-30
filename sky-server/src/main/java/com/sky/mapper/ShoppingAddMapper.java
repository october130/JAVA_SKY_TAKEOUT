package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingAddMapper {
//查询
    List<ShoppingCart> list( ShoppingCart shoppingCart);


    //将购物车已经买过的商品数量加一
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

//插入数据，必须要记得sql里面最终values后面的字段是代表的实体类中的字段，不能写成表中的字段
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);



@Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

@Delete("delete from shopping_cart where id = #{id}")
    void delete(ShoppingCart cart);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
