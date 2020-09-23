package com.murongshian.mvc;

import com.murongshian.mvc.handler.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * DispatcherServlet 所有http请求都由此Servlet转发
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 请求执行链
     */
    private final List<Handler> HANDLER = new ArrayList<>();

    /**
     * 初始化Servlet
     */
    @Override
    public void init() {
        HANDLER.add(new PreRequestHandler());
        HANDLER.add(new SimpleUrlHandler(getServletContext()));
        HANDLER.add(new JspHandler(getServletContext()));
        HANDLER.add(new ControllerHandler());
    }

    /**
     * 执行请求
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        RequestHandlerChain handlerChain = new RequestHandlerChain(HANDLER.iterator(), req, resp);
        handlerChain.doHandlerChain();
        handlerChain.doRender();
    }
}