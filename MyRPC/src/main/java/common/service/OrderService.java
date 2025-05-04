package common.service;

import common.entity.Order;

public interface OrderService {
    Order getOrderByOrderId(String orderId);

    String insertOrder(Order order);
}
