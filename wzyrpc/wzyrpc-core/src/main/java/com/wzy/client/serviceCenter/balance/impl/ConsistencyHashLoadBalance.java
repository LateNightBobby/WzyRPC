package com.wzy.client.serviceCenter.balance.impl;

import com.wzy.client.serviceCenter.balance.LoadBalance;

import java.util.*;

public class ConsistencyHashLoadBalance implements LoadBalance {
    //虚拟结点个数
    private static final int VIRTUAL_NUM = 5;
    //虚拟结点分配，hash值 - 虚拟节点服务器名称
    private SortedMap<Integer, String> shards = new TreeMap<>();
    //真实结点列表
    private static List<String> realNodes = new LinkedList<>();

//    private final Map<String, SortedMap<Integer, String>> serviceShards = new ConcurrentHashMap<>();
//    private final Map<String, List<String>> serviceRealNodes = new ConcurrentHashMap<>();
    //模拟初始服务器
    private String[] servers = null;

    private void init(List<String> serverList) {
//        SortedMap<Integer, String> shards = new TreeMap<>();
//        List<String> realNodes = new ArrayList<>();
        for (String server: serverList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");

            for (int i = 0; i < VIRTUAL_NUM; ++i) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
//        serviceShards.put(serviceName, shards);
//        serviceRealNodes.put(serviceName, realNodes);
    }

    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }

    @Override
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }

    public String getServer(String node, List<String> serviceList) {
        if (shards.isEmpty()) {
            init(serviceList);
        }
        int hash = getHash(node);
        Integer key = null;
        for (Integer k: shards.keySet()) {
            System.out.println(shards.get(k));
        }
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.lastKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    //FNV1_32_HASH
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
