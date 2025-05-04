package com.wzy.client.serviceCenter;

import java.net.InetSocketAddress;

public interface ServiceCenter {
    boolean checkRetry(String serviceName);

    //  查询：根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
}
