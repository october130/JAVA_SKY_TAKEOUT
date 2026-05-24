package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDish(SetmealDTO setmealDTO);

    void startORend(Integer status, long id);

    void delete(List<Long> ids);

    void update(SetmealDTO setmealDTO);

     SetmealVO getByIdWithDish(Long id);
}
