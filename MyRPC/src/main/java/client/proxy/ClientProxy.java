package client.proxy;

import client.circuitBreaker.CircuitBreakProvider;
import client.circuitBreaker.CircuitBreaker;
import client.retry.GuavaRetry;
import client.rpcClient.RpcClient;
import client.rpcClient.impl.NettyRpcClient;
import client.serviceCenter.ServiceCenter;
import client.serviceCenter.impl.ZKServiceCenter;
import common.message.RpcRequest;
import common.message.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

//    //请求对应的ip以及端口号
//    private String host;
//    private int port;

    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakProvider circuitBreakProvider;

//    public ClientProxy(String host,int port,int choose){
//        switch (choose){
//            case 0:
//                rpcClient=new NettyRpcClient(host,port);
//                break;
//            case 1:
//                rpcClient=new SocketRpcClient(host,port);
//        }
//    }
    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZKServiceCenter();
        rpcClient=new NettyRpcClient(serviceCenter);
        circuitBreakProvider = new CircuitBreakProvider();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建rpc请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        //判断是否允许通过
        CircuitBreaker circuitBreaker=circuitBreakProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.allowRequest()) {
//            log.warn("熔断器开启，请求被拒绝: {}", rpcRequest);
            System.out.println("请求被熔断器拒绝:" + rpcRequest);
            return null;
        }

        RpcResponse rpcResponse;
        if (serviceCenter.checkRetry(rpcRequest.getInterfaceName())) {
            rpcResponse = new GuavaRetry().sendServiceWithRetry(rpcRequest, rpcClient);
        } else {
            rpcResponse = rpcClient.sendRequest(rpcRequest);
        }
//        System.out.println(rpcResponse);
        if (rpcResponse.getCode() == 200) {
            circuitBreaker.recordSuccess();
        }
        if (rpcResponse.getCode() == 500) {

            circuitBreaker.recordFailure();
        }
        return rpcResponse.getData();
    }

    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }

    public void close() {
        rpcClient.close();
    }
}
