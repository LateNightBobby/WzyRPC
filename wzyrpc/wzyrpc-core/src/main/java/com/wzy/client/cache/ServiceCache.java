package com.wzy.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCache {
    //key: serviceName 服务名
    //value： addressList 服务提供者列表
    private static Map<String, List<String>> cache = new HashMap<>();

    //添加服务
    public void addServiceToCache(String serviceName, String address) {
        List<String> addressList = cache.get(serviceName);
        if (addressList != null) {
            addressList.add(address);
            System.out.println("将name为"+serviceName+"和地址为"+address+"的服务添加到本地缓存中");
        } else {
            addressList = new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName, addressList);
        }
    }

    //缓存中获取地址
    public List<String> getServiceFromCache(String serviceName) {
        List<String> services = cache.get(serviceName);
        if (services == null) {
            return null;
        }
        return services;
    }

    //从缓存中删除服务地址
    public void delete(String serviceName,String address){
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        if (addressList.isEmpty()) {
            cache.remove(serviceName);
        }
        System.out.println("将name为"+serviceName+"和地址为"+address+"的服务从本地缓存中删除");
    }
}
