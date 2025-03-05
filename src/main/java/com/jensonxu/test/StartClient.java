package com.jensonxu.test;

import com.jensonxu.netty.client.NettyClient;
import com.jensonxu.rpc.RPCRequest;
import com.jensonxu.rpc.RPCResponse;

public class StartClient {
    public static void main(String[] args) {
        RPCRequest request = RPCRequest.builder().interfaceName("interface").methodName("hello").build();
        NettyClient client = new NettyClient("127.0.0.1", 8889);
        for (int i = 0; i < 5; i++) {
            int epoch = i + 1;
            RPCResponse response = client.sendMessage(request);
            System.out.println("===> response " + epoch + ": " + response.toString());
        }
    }
}
