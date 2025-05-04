package server;


import common.service.OrderService;
import common.service.impl.OrderServiceImpl;
import common.service.impl.UserServiceImpl;
import server.provider.ServiceProvider;
import server.serverInstance.RpcServer;
import server.serverInstance.impl.NettyRpcServer;
import common.service.UserService;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/11 19:39
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();
        OrderService orderService = new OrderServiceImpl();

        ServiceProvider serviceProvider=new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService, false);
        serviceProvider.provideServiceInterface(orderService, true);

        RpcServer rpcServer=new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}

//docker run -d --name zk -p 2181:2181 zookeeper:3.5.8
//docker exec -it zk bash
//docker rm -f zk

//./bin/zkCli.sh
//ls /MyRPC
//get /MyRPC/common.service.UserService
//create /

//hostname -I
