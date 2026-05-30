package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController( "userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端-订单接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping ("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(  @RequestBody OrdersSubmitDTO ordersSubmitDTO){
       OrderSubmitVO orderSubmitVO =    orderService.submitOrder(ordersSubmitDTO);
       return Result.success(orderSubmitVO);

    }

    /**
     * 订单支付
     *
     * 模拟支付说明：
     * 配置 sky.wechat.mock-pay=true 后，此接口直接标记订单为已支付，
     * 返回模拟的微信支付参数。前端收到响应后跳过 wx.requestPayment()，
     * 直接展示支付成功页面。
     *
     * 真实支付流程中，此接口会调用微信支付统一下单API获取 prepay_id，
     * 返回支付参数给前端，前端再调用 wx.requestPayment() 唤起支付。
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付: {}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }
@GetMapping("/historyOrders")
    @ApiOperation("查看历史订单")
    public Result<PageResult> list(Integer page, Integer pageSize, String status) {
        // 前端未选择状态筛选时会传空字符串，手动转为 null
        Integer statusInt = (status != null && !status.isEmpty()) ? Integer.valueOf(status) : null;
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, statusInt);
        return Result.success(pageResult);
    }

@GetMapping("/orderDetail/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
}

@PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable Long id){
        orderService. cancelOrder(id);
        return Result.success();

}
@PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
}
}
