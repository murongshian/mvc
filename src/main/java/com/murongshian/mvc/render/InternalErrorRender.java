package com.murongshian.mvc.render;

import com.murongshian.mvc.RequestHandlerChain;

import javax.servlet.http.HttpServletResponse;

/**
 * 渲染500
 */
public class InternalErrorRender implements Render {
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
		handlerChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}