package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
         OrderSubmitVO submitOrder (OrdersSubmitDTO ordersSubmitDTO);

         /**
          * 订单支付
          * @param ordersPaymentDTO 支付参数（订单号、支付方式）
          * @return 支付结果（微信支付所需参数）
          */
         OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult pageQuery4User(Integer page, Integer pageSize, Integer status);

    OrderVO details(Long id);

    void cancelOrder(Long id);

    void repetition(Long id);
}
