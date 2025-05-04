package com.wzy.server.serviceRegister;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    //  注册：保存服务与地址。
    void register(String serviceName, InetSocketAddress serviceAddress);
    void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry);

    void offline(String serviceName, InetSocketAddress socketAddress);

}
