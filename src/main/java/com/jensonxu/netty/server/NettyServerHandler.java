package com.jensonxu.netty.server;

import com.jensonxu.rpc.RPCRequest;
import com.jensonxu.rpc.RPCResponse;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RPCRequest request = (RPCRequest) msg;
            logger.info("server receive msg: [{}] ,times:[{}]", request, atomicInteger.getAndIncrement());
            RPCResponse messageFromServer = RPCResponse.builder().message("message from server").build();
            ChannelFuture channelFuture = ctx.writeAndFlush(messageFromServer);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server catch exception",cause);
        ctx.close();
    }
}
