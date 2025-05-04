package com.wzy.client.serviceCenter.balance;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HashRingManager {
    private static final Map<String, ConsistentHashRing> hashRingMap = new ConcurrentHashMap<>();

    public static void updateService(String serviceName, List<String> addressList) {
        hashRingMap.put(serviceName, new ConsistentHashRing(addressList));
    }

    public static String getServer(String serviceName) {
        ConsistentHashRing ring = hashRingMap.get(serviceName);
        if (ring == null) throw new RuntimeException("服务未注册" + serviceName);
        String random= UUID.randomUUID().toString();
        return ring.getServer(random);
    }

    public static void addNodeToService(String serviceName, String address) {

    }
}
