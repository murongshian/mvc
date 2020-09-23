package com.murongshian.aop.advice;

import java.lang.reflect.Method;

/**
 * 异常通知接口
 */
public interface ThrowsAdvice extends Advice {

    /**
     * 异常方法
     *
     * @param clz    目标类
     * @param method 目标方法
     * @param args   目标方法参数
     * @param e      抛出异常
     */
    void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e);
}
