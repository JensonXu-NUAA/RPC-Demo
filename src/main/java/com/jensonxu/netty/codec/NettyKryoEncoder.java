package com.jensonxu.netty.codec;

import com.jensonxu.utils.serialize.Serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    private final Class<?> genericClass;

    /*
     * 将对象转换为字节码然后写入到 ByteBuf 对象中
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        if(this.genericClass.isInstance(o)) {
            byte[] body = serializer.serialize(o);  // 1. 将对象转换为byte
            int len = body.length;  // 2. 读取消息的长度
            byteBuf.writeInt(len);  // 3. 写入消息对应的字节数组长度，writerIndex加4
            byteBuf.writeBytes(body);  // 4.将字节数组写入ByteBuf对象中
        }
    }
}
