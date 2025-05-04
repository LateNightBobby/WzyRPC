package client;


import client.proxy.ClientProxy;
import common.entity.Order;
import common.entity.User;
import common.service.OrderService;
import common.service.UserService;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/6 18:39
 */
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
//        ClientProxy clientProxy=new ClientProxy("127.0.0.1",9999, 0);
        ClientProxy clientProxy = new ClientProxy();
        UserService userProxy=clientProxy.getProxy(UserService.class);
        OrderService orderProxy = clientProxy.getProxy(OrderService.class);

        User user = userProxy.getUserByUserId(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u=User.builder().id(100).userName("wzy").sex(true).build();
        Integer id = userProxy.insertUserId(u);
        System.out.println("向服务端插入user的id"+id);

        Order order = orderProxy.getOrderByOrderId("wzy1234");
        System.out.println("服务端返回订单order: " + order);

        String orderId = orderProxy.insertOrder(order);
        System.out.println("向服务端插入order的id" + orderId);


        clientProxy.close();
    }
}
