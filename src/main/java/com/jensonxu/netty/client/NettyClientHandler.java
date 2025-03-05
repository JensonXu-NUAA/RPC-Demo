package com.jensonxu.netty.client;

import com.jensonxu.rpc.RPCResponse;

import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            RPCResponse response = (RPCResponse) msg;
            logger.info("client receive msg: [{}]", response.toString());

            // 将服务端的返回结果保存到AttributeMap上
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("response");
            ctx.channel().attr(key).set(response);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
