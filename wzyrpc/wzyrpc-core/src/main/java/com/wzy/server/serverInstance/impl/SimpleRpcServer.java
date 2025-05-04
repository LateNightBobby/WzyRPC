package com.wzy.server.serverInstance.impl;

import lombok.AllArgsConstructor;
import com.wzy.server.provider.ServiceProvider;
import com.wzy.server.serverInstance.RpcServer;
import com.wzy.server.serverInstance.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.format("服务器启动在端口 %d \n", port);
            while (true) {
                // 无连接会阻塞
                Socket socket = serverSocket.accept();
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
