package com.murongshian.mvc.handler;

import com.murongshian.mvc.RequestHandlerChain;

/**
 * 请求预处理
 */
public class PreRequestHandler implements Handler {
    @Override
    public boolean handle(final RequestHandlerChain handlerChain) throws Exception {
        // 设置请求编码方式
        handlerChain.getRequest().setCharacterEncoding("UTF-8");
        String requestPath = handlerChain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            handlerChain.setRequestPath(requestPath.substring(0, requestPath.length() - 1));
        }
        System.out.println("[Doodle] " + handlerChain.getRequestMethod() + handlerChain.getRequestPath());
        return true;
    }
}