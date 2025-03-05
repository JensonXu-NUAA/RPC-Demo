package com.jensonxu.utils.serialize;

import com.jensonxu.rpc.RPCRequest;
import com.jensonxu.rpc.RPCResponse;
import com.jensonxu.utils.exception.SerializeException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class KryoSerializer implements Serializer {
    /*
     * Because Kryo is not thread safe. So, use ThreadLocal to store Kryo objects
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RPCResponse.class);
        kryo.register(RPCRequest.class);
        kryo.setReferences(true);  // 默认值为true，是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为true
        kryo.setRegistrationRequired(false);  // 默认值为false，是否关闭循环引用，可以提高性能，但是一般不推荐设置为true
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = this.kryoThreadLocal.get();
            kryo.writeObject(output, obj);  // Object->byte:将对象序列化为byte数组
            this.kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.error("Serialization failed", e);
            throw new SerializeException("Serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = this.kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            this.kryoThreadLocal.remove();
            return clazz.cast(obj);  // byte->Object:从byte数组中反序列化出对对象
        } catch (Exception e) {
            log.error("Deserialization failed", e);
            throw new SerializeException("Deserialization failed", e);
        }
    }


}
