package com.murongshian.mvc.render;

import com.murongshian.mvc.RequestHandlerChain;

/**
 * 默认渲染 200
 */
public class DefaultRender implements Render {
    @Override
    public void render(RequestHandlerChain handlerChain) {
        int status = handlerChain.getResponseStatus();
        handlerChain.getResponse().setStatus(status);
    }
}