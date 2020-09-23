package com.murongshian;

/**
 * 服务器相关配置
 */
public class Configuration {
    /**
     * 启动类
     */
    private Class<?> bootClass;

    /**
     * 资源目录
     */
    private String resourcePath;

    /**
     * jsp目录
     */
    private String viewPath;

    /**
     * 静态文件目录
     */
    private String assetPath;

    /**
     * 端口号
     */
    private int serverPort;

    /**
     * tomcat docBase目录
     */
    private String docBase;

    /**
     * tomcat contextPath目录
     */
    private String contextPath;

    public Configuration(Class<?> bootClass,String resourcePath,String viewPath,String assetPath,int serverPort,String docBase,String contextPath){
        this.bootClass = bootClass;
        this.resourcePath = resourcePath;
        this.viewPath = viewPath;
        this.assetPath = assetPath;
        this.serverPort = serverPort;
        this.docBase = docBase;
        this.contextPath = contextPath;
    }

    public Class<?> getBootClass() {
        return bootClass;
    }

    public String getViewPath() {
        return viewPath;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getDocBase() {
        return docBase;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public static Configuration.ConfigurationBuilder builder(){
        return new Configuration.ConfigurationBuilder();
    }

    public static class ConfigurationBuilder{

        private Class<?> bootClass;

        private String resourcePath = "src/main/resources/";

        private String viewPath = "/templates/";

        private String assetPath = "/static/";

        private int serverPort = 10000;

        private String docBase = "";

        private String contextPath = "";

        ConfigurationBuilder(){}

        public Configuration.ConfigurationBuilder bootClass(Class<?> bootClass){
            this.bootClass = bootClass;
            return this;
        }

        public Configuration.ConfigurationBuilder resourcePath(String resourcePath){
            this.resourcePath = resourcePath;
            return this;
        }

        public Configuration.ConfigurationBuilder viewPath(String viewPath){
            this.viewPath = viewPath;
            return this;
        }

        public Configuration.ConfigurationBuilder assetPath(String assetPath){
            this.assetPath = assetPath;
            return this;
        }

        public Configuration.ConfigurationBuilder serverPort(int serverPort){
            this.serverPort = serverPort;
            return this;
        }

        public Configuration.ConfigurationBuilder docBase(String docBase){
            this.docBase = docBase;
            return this;
        }

        public Configuration.ConfigurationBuilder contextPath(String contextPath){
            this.contextPath = contextPath;
            return this;
        }

        public Configuration build(){
            return new Configuration(bootClass,resourcePath,viewPath,assetPath,serverPort,docBase,contextPath);
        }
    }
}