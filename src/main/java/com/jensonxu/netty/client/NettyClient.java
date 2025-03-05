package com.jensonxu.netty.client;

import com.jensonxu.netty.codec.NettyKryoDecoder;
import com.jensonxu.netty.codec.NettyKryoEncoder;
import com.jensonxu.rpc.RPCRequest;
import com.jensonxu.rpc.RPCResponse;
import com.jensonxu.utils.serialize.KryoSerializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *   Netty客户端
 */
public class NettyClient {
    private final String host;
    private final int port;
    private static final Bootstrap boostrap;
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源
    static {
        boostrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();

        boostrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RPCResponse.class));
                        channel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RPCRequest.class));
                        channel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     *
     * @param request 消息体
     * @return 服务端返回的数据
     */
    public RPCResponse sendMessage(RPCRequest request) {
        try {
            ChannelFuture channelFuture = boostrap.connect(host, port).sync();
            logger.info("client connect  {}", host + ":" + port);
            Channel channel = channelFuture.channel();
            logger.info("send message ...");

            if (channel != null) {
                channel.writeAndFlush(request).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("client send message: [{}]", request.toString());
                    } else {
                        logger.error("Send failed:", future.cause());
                    }
                });

                //阻塞等待 ，直到Channel关闭
                channel.closeFuture().sync();
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("response");
                return channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
