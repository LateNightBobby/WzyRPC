package server.provider;

import server.serviceRegister.ServiceRegister;
import server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    //存放服务实例
    private Map<String, Object> interfaceProvider;

    private String host;
    private int port;

    private ServiceRegister serviceRegister;

    public ServiceProvider (String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }

    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        // 所有实现的接口
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        for (Class<?> clazz: interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }
    }

    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
