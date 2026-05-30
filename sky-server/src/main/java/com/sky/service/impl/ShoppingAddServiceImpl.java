package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingAddMapper;
import com.sky.service.ShoppingAddService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShoppingAddServiceImpl implements ShoppingAddService {

    @Autowired
    private ShoppingAddMapper shoppingAddMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();//首先，将shoppingCartDTO对象中的数据复制到shoppingCart对象中
        //需要在shoppingcart那个表里面插，还有最终的插入操作
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentId = BaseContext.getCurrentId();//jwt中获取用户id，直接套用
        log.info("当前用户id为：{}", currentId);
        shoppingCart.setUserId(currentId);//添加用户id

        List<ShoppingCart> list = shoppingAddMapper.list(shoppingCart);//mapper层查询，
        // 根据用户id和菜品id或者套餐id查询，如果查询结果不为空，则数量加1，否则插入数据
        if(list!=null && list.size()>0){//如果在数据库查到数据了,证明购物车已经有这个东西了，则数量加1
            ShoppingCart cart = list.get(0);//get(0)是为了获取数据
            cart.setNumber(cart.getNumber()+1);
            shoppingAddMapper.updateNumberById(cart);
        }
        else{//如果数据库中没有数据，则证明购物车里面还没有数据，得重新插入
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            //在插入数据之前需要判断点的是菜品还是套餐
            if(dishId != null){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);//封装到shoppingCart对象中，购物车里面
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());

            }else {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                if (setmeal == null) {
                    log.error("添加购物车失败：套餐不存在，setmealId={}", setmealId);
                    return;
                }
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
            }

            shoppingCart.setCreateTime(LocalDateTime.now());//最后其他数据设定完成之后还要有时间创建
            shoppingAddMapper.insert(shoppingCart);//插入数据

        }
    }

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
     ShoppingCart shoppingCart =   ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingAddMapper.list(shoppingCart);//这里查看购物车商品所用Mapper中List方法
        //和购物车添加商品的逻辑一样，都是根据用户id查询
 log.info("查询购物车结果：{}", list);
        return list;
     }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingAddMapper.deleteByUserId(userId);

    }

    @Override
    public void subtract(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentId = BaseContext.getCurrentId();//jwt中获取用户id，直接套用
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingAddMapper.list(shoppingCart);//mapper层查询，该用户id下购物车数据
        if(list!=null && list.size()>0) {
            ShoppingCart cart = list.get(0);//这个还是没搞懂为什么是get(0)

            cart.setNumber(cart.getNumber() - 1);
            if (cart.getNumber() > 0) {
                shoppingAddMapper.updateNumberById(cart);//购物车中重复商品数量减一
            } else {
                shoppingAddMapper.delete(cart);
            }
            log.info("购物车数据：{}", cart);
        }
        else{
            log.info("购物车数据不存在");
        }




    }
}
