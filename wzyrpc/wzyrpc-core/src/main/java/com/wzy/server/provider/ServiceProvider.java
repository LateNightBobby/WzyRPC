package com.wzy.server.provider;

import com.wzy.server.rateLimit.provider.RateLimitProvider;
import com.wzy.server.serviceRegister.ServiceRegister;
import com.wzy.server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProvider {
    //存放服务实例
    private Map<String, Object> interfaceProvider;

    private String host;
    private int port;

    private List<String> serviceNames;

    private ServiceRegister serviceRegister;

    private RateLimitProvider rateLimitProvider;

    public ServiceProvider (String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
        this.serviceNames = new ArrayList<>();
        this.rateLimitProvider = new RateLimitProvider();
    }

    public void provideServiceInterface(Object service, boolean canRetry) {
        String serviceName = service.getClass().getName();
        serviceNames.add(serviceName);
        // 所有实现的接口
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        for (Class<?> clazz: interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }

    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

    public void shutDown() {
        for (String serviceName : serviceNames) {
            serviceRegister.offline(serviceName, new InetSocketAddress(host, port));
        }
    }

    public RateLimitProvider getRateLimitProvider() {
        return rateLimitProvider;
    }
}
