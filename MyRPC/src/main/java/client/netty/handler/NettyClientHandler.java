package client.netty.handler;

import common.entity.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import common.message.RpcResponse;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/26 17:29
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // 接收到response, 给channel设计别名，让sendRequest里读取response
        AttributeKey<RpcResponse> key = AttributeKey.valueOf(Constants.RPCRESPONSE_KEY);
        ctx.channel().attr(key).set(response);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常处理
        cause.printStackTrace();
        ctx.close();
    }
}
