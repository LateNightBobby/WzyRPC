package com.wzy.consumer;

import com.wzy.client.proxy.ClientProxy;
import com.wzy.entity.Order;
import com.wzy.entity.User;
import com.wzy.service.OrderService;
import com.wzy.service.UserService;

public class ConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService userProxy=clientProxy.getProxy(UserService.class);
        OrderService orderProxy = clientProxy.getProxy(OrderService.class);

        for (int i = 0; i < 120; ++i) {
            Integer i1 = i;
            if (i % 30 == 0) {
                Thread.sleep(10000);
            }
            new Thread(() -> {
                try {
                    User user = userProxy.getUserByUserId(i1);
                    System.out.println("从服务端得到的user="+user.toString());

                    User u=User.builder().id(100).userName("wzy").sex(true).build();
                    Integer id = userProxy.insertUserId(u);
                    System.out.println("向服务端插入user的id"+id);

                    Order order = orderProxy.getOrderByOrderId("" + i1);
                    System.out.println("服务端返回订单order: " + order);

                    String orderId = orderProxy.insertOrder(order);
                    System.out.println("向服务端插入order的id" + orderId);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        clientProxy.close();
    }
}
