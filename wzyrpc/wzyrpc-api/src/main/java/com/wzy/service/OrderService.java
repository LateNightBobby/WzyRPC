package com.wzy.service;

import com.wzy.entity.Order;

public interface OrderService {
    Order getOrderByOrderId(String orderId);

    String insertOrder(Order order);
}
