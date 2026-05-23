package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.Dishservice;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl  implements Dishservice {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


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

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        log.info("分页查询结果：{}", page);
        long total = page.getTotal();
        List<Dish> records = page.getResult();



        return new PageResult(total, records);
    }

    @Override
    public void delete(List<Long> ids) {
        //1.在售的菜品是不能够删除的,所以先执行查询操作
        for (Long id : ids) {
          Dish dish =  dishMapper.getById(id);//获取当前餐品状态
          if (dish.getStatus() == 1){
              throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);//抛出异常
          }
        }
//2.如果当前菜品有关联套餐在售，删除菜品是不允许的，然后在另一个mapper里执行查询
         List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);//获取当前菜品关联的套餐id
        if (setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0){
            //当前菜品有在售的套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
// 3.如果上述情况都没有，则删除菜品数据
        dishMapper.delete(ids);

        // 4 .同样的，菜品添加时口味数据也跟着一块删除

        dishFlavorMapper.deleteByDishId(ids);


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);

      List<DishFlavor> dishFlavors =  dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);//将dish中的数据复制给dishVO对象
        dishVO.setFlavors(dishFlavors);


        return  dishVO;
    }

    @Override
    public void updateflavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);//因为当前只是修改菜品基本信息，所以用dishDTO来获取，不包括风味修改
        dishMapper.update(dish);


        //这一步就是删除菜品口味数据
        Long id = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(Collections.singletonList(id));//但是因为风味删除那个方法是批量删除，所以只能用单list集合

//重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();//获取菜品口味数据,将菜品口味数据封装集合
        // ，因为只有dishdto中有菜品口味数据，所以用dishdto来获取
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);//将菜品id设置给菜品口味对象
            }
            dishFlavorMapper.insertBatch(flavors);//批量插入菜品口味数据
        }

    }

    @Override
    public void StartOrEnd(Integer status, Long id) {
         Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        log.info("修改菜品状态成功：{}", dish);
                dishMapper.update(dish);


    }

    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        return dishMapper.listByCategoryId(categoryId);
    }


}
