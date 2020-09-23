package com.murongshian.mvc.server;

/**
 * 服务器 interface
 */
public interface Server {
    /**
     * 启动服务器
     *
     * @throws Exception Exception
     */
    void startServer() throws Exception;

    /**
     * 停止服务器
     *
     * @throws Exception Exception
     */
    void stopServer() throws Exception;
}