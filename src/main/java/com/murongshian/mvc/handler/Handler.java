package com.murongshian.mvc.handler;

import com.murongshian.mvc.RequestHandlerChain;

/**
 * 请求执行器 Handler
 */
public interface Handler {
    /**
     * 请求的执行器
     */
    boolean handle(final RequestHandlerChain handlerChain) throws Exception;
}