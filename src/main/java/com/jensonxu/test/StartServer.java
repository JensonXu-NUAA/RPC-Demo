package com.jensonxu.test;

import com.jensonxu.netty.server.NettyServer;

public class StartServer {
    public static void main(String[] args) {
        new NettyServer(8889).run();
    }
}