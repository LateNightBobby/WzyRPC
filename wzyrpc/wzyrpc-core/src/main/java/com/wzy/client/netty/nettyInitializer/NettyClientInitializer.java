package com.wzy.client.netty.nettyInitializer;


import com.wzy.client.netty.handler.NettyClientHandler;
import common.serializer.myEncoderDecoder.MyDecoder;
import common.serializer.myEncoderDecoder.MyEncoder;
import common.serializer.mySerializer.ObjectSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/26 17:26
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        //消息格式 【长度】【消息体】，解决沾包问题
//        pipeline.addLast(
//                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
//        //计算当前待发送消息的长度，写入到前4个字节中
//        pipeline.addLast(new LengthFieldPrepender(4));
//        //编码器
//        //使用Java序列化方式，netty的自带的解码编码支持传输这种结构
//        pipeline.addLast(new ObjectEncoder());
//        //解码器
//        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
//        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
//        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
//            @Override
//            public Class<?> resolve(String className) throws ClassNotFoundException {
//                return Class.forName(className);
//            }
//        }));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new ObjectSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
