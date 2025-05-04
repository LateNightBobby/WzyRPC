package client;

import client.proxy.ClientProxy;
import common.entity.Order;
import common.entity.User;
import common.service.OrderService;
import common.service.UserService;

public class ConcurrentTestClient {
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
