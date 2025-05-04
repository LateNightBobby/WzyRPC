package common.service.impl;

import common.entity.Order;
import common.entity.User;
import common.service.OrderService;

import java.util.Random;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order getOrderByOrderId(String orderId) {
        System.out.println("客户端查询了"+orderId+"的订单");
        Random random = new Random();
        return Order.builder()
                .orderId(orderId)
                .userName(UUID.randomUUID().toString())
                .amount(random.nextInt())
                .createTime(System.currentTimeMillis())
                .build();
    }

    @Override
    public String insertOrder(Order order) {
        System.out.println("插入数据成功"+order.getOrderId());
        return order.getOrderId();
    }
}
