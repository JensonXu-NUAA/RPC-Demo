package com.jensonxu.netty.codec;

import com.jensonxu.utils.serialize.Serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private final Serializer serializer;
    private final Class<?> genericClass;
    private static final int BODY_LENGTH = 4;  // Netty传输的消息长度也就是对象序列化后对应的字节数组的大小，存储在 ByteBuf 头部

    /*
     * 解码 ByteBuf 对象
     *
     * @param ctx 解码器关联的 ChannelHandlerContext 对象
     * @param in  "入站"数据，也就是 ByteBuf 对象
     * @param out 解码之后的数据对象需要添加到 out 对象里面
     */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= BODY_LENGTH) {  // 1. byteBuf中写入的消息长度所占的字节数已经是4了，所以byteBuf的可读字节必须大于4
            in.markReaderIndex();  // 2. 标记当前readIndex的位置，以便后面重置readIndex 的时候使用
            int len = in.readInt();  // 3. 读取消息的长度

            if(len < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");  // 4. 遇到不合理的情况直接return
                return;
            }

            if (in.readableBytes() < len) {  // 5. 如果可读字节数小于消息长度的话，说明是不完整的消息，重置readIndex
                in.resetReaderIndex();
                return;
            }

            byte[] body = new byte[len];
            in.readBytes(body);

            Object obj = serializer.deserialize(body, genericClass);  // 6. 将bytes数组转换为我们需要的对象
            out.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
