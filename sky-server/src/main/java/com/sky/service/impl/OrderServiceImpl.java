package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingAddMapper;
import com.sky.properties.WeChatProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements  OrderService {


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingAddMapper shoppingAddMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
      /*
      业务流程
      1. 异常处理，判断addressBookId是否为空，判断shoppingCart是否为空
      这个需要注入相对的mapper层
      2.向orders表插入一条数据
      3.向orderDetail表插入n条数据
      4.清空购物车数据
      5.返回OrderSubmitVO
       */
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingAddMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }//异常解决

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber( String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPhone( addressBook.getPhone());
        orders.setConsignee( addressBook.getConsignee());
        orders.setUserId(userId);
        orderMapper.insert(orders);
        //order表插入成功

	List<OrderDetail> orderDetailList = new ArrayList<>();//创建集合
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());//获取主键值订单 id
            orderDetailList.add(orderDetail);//批量插入，所以要添加进集合
        }
        orderDetailMapper.insertBatch(orderDetailList);
	//orderDetail表插入成功
	shoppingAddMapper.deleteByUserId(userId);//清空购物车
	return OrderSubmitVO.builder()
	        .id(orders.getId())
	        .orderNumber(orders.getNumber())
	        .orderAmount(orders.getAmount())
	        .orderTime(orders.getOrderTime())
	        .build();//封装返回结果vo对象
    }

    /**
     * 订单支付
     *
     * 模拟支付流程说明：
     * 由于没有商户认证，无法真正对接微信支付。在 application-dev.yml 中配置
     * sky.wechat.mock-pay=true 后，此方法会直接标记订单为"已支付"状态，
     * 跳过微信支付 API 调用，方便前端展示支付成功结果。
     *
     * 后续获得商户认证后，只需：
     * 1. 在 application-dev.yml 中设置 mock-pay=false
     * 2. 补充商户相关配置（mchid, 证书路径等）
     * 3. 启用下方"真实支付模式"的注释代码即可
     */
    @Override
    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 1. 根据订单号查询订单
        Orders orders = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 校验订单状态：只有"待付款"状态的订单才能支付
        if (!Orders.PENDING_PAYMENT.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 3. 模拟支付模式：直接标记支付成功，绕过微信支付接口
        if (Boolean.TRUE.equals(weChatProperties.getMockPay())) {
            // 更新订单支付状态
            orders.setPayStatus(Orders.PAID);
            orders.setStatus(Orders.TO_BE_CONFIRMED); // 支付成功，状态变为"待接单"
            orders.setCheckoutTime(LocalDateTime.now());
            orders.setPayMethod(ordersPaymentDTO.getPayMethod());
            orderMapper.update(orders);

            // 返回模拟的支付参数
            // 前端收到此响应后，跳过 wx.requestPayment()，直接跳转支付成功页
            return OrderPaymentVO.builder()
                    .nonceStr("mock_nonce_str_" + System.currentTimeMillis())
                    .paySign("mock_pay_sign")
                    .timeStamp(String.valueOf(System.currentTimeMillis() / 1000))
                    .signType("RSA")
                    .packageStr("prepay_id=mock_prepay_" + orders.getNumber())
                    .build();
        }

        // 4. 真实支付模式：调用微信支付接口（需要商户认证）
        // ============ 获得商户认证后，取消下方注释即可启用 ============
        // try {
        //     // 查询用户openid
        //     User user = userMapper.getById(orders.getUserId());
        //     // 调用微信支付统一下单接口
        //     JSONObject result = weChatPayUtil.pay(
        //             orders.getNumber(),
        //             orders.getAmount(),
        //             "苍穹外卖订单",
        //             user.getOpenid()
        //     );
        //     // 解析返回的 prepay_id
        //     String prepayId = result.getString("package");
        //     if (prepayId == null) {
        //         throw new OrderBusinessException("微信支付下单失败");
        //     }
        //     // 更新订单状态
        //     orders.setPayStatus(Orders.PAID);
        //     orders.setStatus(Orders.TO_BE_CONFIRMED);
        //     orders.setCheckoutTime(LocalDateTime.now());
        //     orderMapper.update(orders);
        //     // 返回支付参数给前端调起微信支付
        //     return OrderPaymentVO.builder()
        //             .nonceStr(result.getString("nonceStr"))
        //             .paySign(result.getString("paySign"))
        //             .timeStamp(result.getString("timeStamp"))
        //             .signType(result.getString("signType"))
        //             .packageStr(result.getString("package"))
        //             .build();
        // } catch (Exception e) {
        //     throw new OrderBusinessException("微信支付异常: " + e.getMessage());
        // }
        // ============ 真实支付模式结束 ============

        throw new OrderBusinessException("微信支付尚未配置，请在 application-dev.yml 中设置 sky.wechat.mock-pay=true 开启模拟支付");
    }

    @Override
    public PageResult pageQuery4User(Integer page, Integer pageSize, Integer status) {PageHelper.startPage(page, pageSize);//调用PageHelper.startPage()方法，传入页码和每页记录数
       OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);  // 使用传入的status，不是写死为1

       Page<Orders> pageOrder = orderMapper.pageQuery(ordersPageQueryDTO);

       List<OrderVO> orderVOList = new ArrayList<>();
        if (pageOrder.size() > 0 && pageOrder.getResult() != null) {
            for (Orders orders :pageOrder.getResult()) {
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
               BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
               orderVOList.add(orderVO);  // 别忘了添加到列表！
         }
        }
        return new PageResult(pageOrder.getTotal(), orderVOList);
    }

    @Override
    public OrderVO details(Long id) {

        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return  orderVO;
    }

    /**
     * 取消订单
     *
     * 业务逻辑分两种情况：
     * 1. 未付款（payStatus=UN_PAID）：直接取消，无需退款
     * 2. 已付款（payStatus=PAID）：需要先退款再取消
     *    - 真实模式：调用微信退款接口，钱退回用户微信账户
     *    - 模拟模式：直接标记退款（因为没有真实扣款）
     *
     * 可取消条件：只有待付款(1)和待接单(2)状态才能取消
     */
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        // 1. 查询订单
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 校验订单状态：只有待付款(1)和待接单(2)可以取消
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 3. 如果已付款，需要退款
        if (Orders.PAID.equals(orders.getPayStatus())) {
            // ====== 模拟模式：跳过真实退款 ======
            if (Boolean.TRUE.equals(weChatProperties.getMockPay())) {
                orders.setPayStatus(Orders.REFUND);
            } else {
                throw new OrderBusinessException("微信退款尚未配置，请在 application-dev.yml 中设置 mock-pay=true");
            }
        }

        // 4. 更新订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {//这个id是订单id
        Long customerId = BaseContext.getCurrentId();//获取当前用户id
        ArrayList<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(customerId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());//把订单详情转换成购物车对象
        shoppingAddMapper. insertBatch(shoppingCartList);//批量插入


    }
}