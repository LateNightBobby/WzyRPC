package client.serviceCenter.impl;

import client.cache.ServiceCache;
import client.serviceCenter.ServiceCenter;
import client.serviceCenter.balance.LoadBalance;
import client.serviceCenter.balance.impl.ConsistencyHashLoadBalance;
import client.serviceCenter.watcher.WatchZK;
import common.entity.Constants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ZKServiceCenter implements ServiceCenter {

    private CuratorFramework client;

    private ServiceCache cache;

    private LoadBalance loadBalance = new ConsistencyHashLoadBalance();

    public ZKServiceCenter() throws InterruptedException {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString(Constants.ZK_ADDRESS)
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(Constants.ROOT_PATH).build();
        this.client.start();
        this.client.blockUntilConnected();
        System.out.println("zookeeper ServiceCenter 连接成功");

        cache = new ServiceCache();
        WatchZK watchZK = new WatchZK(client, cache);
        watchZK.watchToUpdate(Constants.ROOT_PATH);

    }

    //保证线程安全使用CopyOnWriteArraySet
//    private Set<String> retryServiceCache = new CopyOnWriteArraySet<>();
    //写一个白名单缓存，优化性能
//    @Override
//    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature) {
//        if (retryServiceCache.isEmpty()) {
//            try {
//                CuratorFramework rootClient = client.usingNamespace(RETRY);
//                List<String> retryableMethods = rootClient.getChildren().forPath("/" + getServiceAddress(serviceAddress));
//                retryServiceCache.addAll(retryableMethods);
//            } catch (Exception e) {
//                log.error("检查重试失败，方法签名：{}", methodSignature, e);
//            }
//        }
//        return retryServiceCache.contains(methodSignature);
//    }
    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + Constants.RETRY_PATH);
            for (String service: serviceList) {
                if (service.equals(serviceName)) {
                    System.out.println("服务"+serviceName+"在白名单上，可进行重试");
                    canRetry = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canRetry;
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // 先找缓存
            List<String> serviceList = cache.getServiceFromCache(serviceName);

            if (serviceList == null) {
                System.out.println("尝试访问路径: " + client.getNamespace() + "/" + serviceName);
                serviceList = client.getChildren().forPath("/" + serviceName);
            }

//            List<String> strings = client.getChildren().forPath("/" + serviceName);
//            TODO 负载均衡 现默认使用第一个 hh
            String address = loadBalance.balance(serviceList);
//            String address = HashRingManager.getServer(serviceName);
            return parseAddress(address);
//            return parseAddress(serviceList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }
    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
