package server.serviceRegister.impl;

import common.entity.Constants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import server.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;

public class ZKServiceRegister implements ServiceRegister {

    private CuratorFramework client;

    public ZKServiceRegister() {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString(Constants.ZK_ADDRESS)
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(Constants.ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper ServiceRegister 连接成功");
    }
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                System.out.format("创建服务 %s\n", serviceName);
            }
            // 创建路径地址
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            //
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            System.out.format("created node: %s\n", path);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("此服务已存在");
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                System.out.format("创建服务 %s\n", serviceName);
            }
            // 创建路径地址
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            //
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            System.out.format("created node: %s\n", path);

            //幂等服务添加到retry中
            if (canRetry) {
                path = "/" + Constants.RETRY_PATH + "/" + serviceName;
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("此服务已存在");
        }
    }

    @Override
    public void offline(String serviceName, InetSocketAddress socketAddress) {
        String path = "/" + serviceName + "/" + getServiceAddress(socketAddress);
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
