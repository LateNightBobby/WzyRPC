package client.rpcClient.impl;

import client.rpcClient.RpcClient;
import client.netty.nettyInitializer.NettyClientInitializer;
import client.serviceCenter.ServiceCenter;
import client.serviceCenter.impl.ZKServiceCenter;
import common.entity.Constants;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class NettyRpcClient implements RpcClient {

//    private String host;
//    private int port;

//    bootstrap：Netty 的客户端启动引导类，用于配置和创建连接；
//    eventLoopGroup：Netty 的事件处理线程组，负责 I/O 事件，如连接、读取、写入等。
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    private ServiceCenter serviceCenter;

//    public NettyRpcClient(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }
    public NettyRpcClient() {
        this.serviceCenter = new ZKServiceCenter();
    }

    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                //NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        // 先向注册中心获取host和port
        InetSocketAddress address = serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host = address.getHostName();
        int port = address.getPort();
        try {
            //创建一个channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //创建一个连接单位 channel 类似socket
            Channel channel = channelFuture.channel();
            // 写入请求
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            //
            AttributeKey<RpcResponse> key = AttributeKey.valueOf(Constants.RPCRESPONSE_KEY);
            RpcResponse response = channel.attr(key).get();

            return response;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return RpcResponse.fail();
        }
    }

}
