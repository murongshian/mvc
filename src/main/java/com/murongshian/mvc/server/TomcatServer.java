package com.murongshian.mvc.server;

import com.murongshian.Configuration;
import com.murongshian.mvc.DispatcherServlet;
import com.murongshian.Doodle;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * Tomcat 服务器
 */
public class TomcatServer implements Server {

    private Tomcat tomcat;

    public TomcatServer() {
        new TomcatServer(Doodle.getConfiguration());
    }

    public TomcatServer(Configuration configuration) {
        try {
            this.tomcat = new Tomcat();
            //tomcat存储自身信息，保存在docBase目录下
            tomcat.setBaseDir(configuration.getDocBase());
            tomcat.setPort(configuration.getServerPort());

            File root = getRootFolder(configuration);
            File webContentFolder = new File(root.getAbsolutePath(), configuration.getResourcePath());
            if (!webContentFolder.exists()) {
                webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
            }

            System.out.println("Tomcat:configuring app with basedir: " + webContentFolder.getAbsolutePath());
            StandardContext ctx = (StandardContext) tomcat.addWebapp(configuration.getContextPath(), webContentFolder.getAbsolutePath());
            ctx.setParentClassLoader(this.getClass().getClassLoader());

            WebResourceRoot resources = new StandardRoot(ctx);
            ctx.setResources(resources);

            // 去除了JspHandler和SimpleUrlHandler这两个servlet的注册

            tomcat.addServlet(configuration.getContextPath(), "dispatcherServlet", new DispatcherServlet()).setLoadOnStartup(0);
            ctx.addServletMappingDecoded("/*", "dispatcherServlet");
        } catch (Exception e) {
            System.err.println("初始化Tomcat失败" + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startServer() throws Exception {
        tomcat.start();
        String address = tomcat.getServer().getAddress();
        int port = tomcat.getConnector().getPort();
        System.out.println("local address: http://" + address + port);
        tomcat.getServer().await();
    }

    @Override
    public void stopServer() throws Exception {
        tomcat.stop();
    }

    private File getRootFolder(Configuration configuration) {
        try {
            File root;
            String runningJarPath = configuration.getBootClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            System.out.println("Tomcat:application resolved root folder: " + root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}