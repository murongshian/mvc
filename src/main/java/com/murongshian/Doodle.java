package com.murongshian;

import com.murongshian.core.BeanContainer;
import com.murongshian.ioc.Ioc;
import com.murongshian.mvc.server.TomcatServer;
import com.murongshian.aop.Aop;
import com.murongshian.mvc.server.Server;

/**
 * Doodle Starter
 */
public final class Doodle {
    private Doodle(){}
    /**
     * 全局配置
     */
    private static Configuration configuration = Configuration.builder().build();

    /**
     * 默认服务器
     */
    private static Server server;

    /**
     * 启动
     *
     * @param bootClass 启动服务器的类
     */
    public static void run(Class<?> bootClass) {
        run(Configuration.builder().bootClass(bootClass).build());
    }

    /**
     * 启动
     *
     * @param bootClass 启动服务器的类
     * @param port      服务器端口
     */
    public static void run(Class<?> bootClass, int port) {
        new Doodle().start(Configuration.builder().bootClass(bootClass).serverPort(port).build());
    }

    /**
     * 启动
     *
     * @param configuration 配置
     */
    public static void run(Configuration configuration) {
        new Doodle().start(configuration);
    }

    /**
     * 获取server
     *
     * @return 项目服务器
     */
    public static Server getServer() {
        return server;
    }

    /**
     * 获取全局配置
     *
     * @return 全局配置
     */
    public static Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 初始化
     *
     * @param configuration 配置
     */
    private void start(Configuration configuration) {
        try {
            Doodle.configuration = configuration;
            //获取框架启动器入口所在的包名称
            String basePackage = configuration.getBootClass().getPackage().getName();
            BeanContainer.getInstance().loadBeans(basePackage);

            new Aop().doAop();
            new Ioc().doIoc();

            server = new TomcatServer(configuration);
            server.startServer();
        } catch (Exception e) {
            System.err.println("Doodle 启动失败\n" + e);
        }
    }
}