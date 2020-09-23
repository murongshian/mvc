package com.murongshian.mvc.handler;

import com.murongshian.mvc.RequestHandlerChain;
import com.murongshian.Doodle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * 普通url请求执行
 * 主要处理静态资源
 */
public class SimpleUrlHandler implements Handler {
    /**
     * tomcat默认RequestDispatcher的名称
     * TODO: 其他服务器默认的RequestDispatcher.如WebLogic为FileServlet
     */
    private static final String TOMCAT_DEFAULT_SERVLET = "default";

    /**
     * 默认的RequestDispatcher,处理静态资源
     */
    private RequestDispatcher defaultServlet;

    public SimpleUrlHandler(ServletContext servletContext) {
        defaultServlet = servletContext.getNamedDispatcher(TOMCAT_DEFAULT_SERVLET);
        if (null == defaultServlet) {
            throw new RuntimeException("没有默认的Servlet");
        }
        System.out.println("The default servlet for serving static resource is " + TOMCAT_DEFAULT_SERVLET);
    }

    @Override
    public boolean handle(final RequestHandlerChain handlerChain) throws Exception {
        if (isStaticResource(handlerChain.getRequestPath())) {
            defaultServlet.forward(handlerChain.getRequest(), handlerChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 是否为静态资源
     */
    private boolean isStaticResource(String url) {
        return url.startsWith(Doodle.getConfiguration().getAssetPath());
    }
}