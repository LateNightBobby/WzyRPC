package com.wzy.client.serviceCenter.balance;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRing {

    public static final int VIRTUAL_NUM = 5;
    private final SortedMap<Integer, String> ring  = new TreeMap<>();

    public ConsistentHashRing(List<String> addressList) {
        for (String address: addressList) {
            for (int i = 0; i < VIRTUAL_NUM; ++i) {
                String virtual_node = address + "&&VN" + i;
                int hash = getHash(address);
                ring.put(hash, virtual_node);
            }
        }
    }

    public String getServer(String node) {
        int hash = getHash(node);
        //使用ring中第一个大于hash的服务
        SortedMap<Integer, String> subMap = ring.tailMap(hash);
        Integer key = subMap.isEmpty() ? ring.lastKey() : subMap.firstKey();
        String virtualNode = ring.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    //FNV1_32_HASH
    private int getHash(String str) {
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
