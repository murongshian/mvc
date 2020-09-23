package com.murongshian.aop;

import com.murongshian.aop.advice.Advice;
import com.murongshian.aop.advice.AfterReturningAdvice;
import com.murongshian.aop.advice.MethodBeforeAdvice;
import com.murongshian.aop.advice.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * 代理通知类
 */
public class ProxyAdvisor implements Comparable<ProxyAdvisor>{

    /**
     * 通知
     */
    private Advice advice;
    /**
     * AspectJ表达式切点匹配器
     */
    private ProxyPointcut pointcut;
    /**
     * 执行顺序
     */
    private int order;
    /**
     * 执行代理方法
     */
    public Object doProxy(AdviceChain adviceChain) throws Throwable {
        Object result = null;
        Class<?> targetClass = adviceChain.getTargetClass();
        Method method = adviceChain.getMethod();
        Object[] args = adviceChain.getArgs();

        if (advice instanceof MethodBeforeAdvice) {
            ((MethodBeforeAdvice) advice).before(targetClass, method, args);
        }
        try {
            result = adviceChain.doAdviceChain(); //执行代理链方法
            if (advice instanceof AfterReturningAdvice) {
                ((AfterReturningAdvice) advice).afterReturning(targetClass, result, method, args);
            }
        } catch (Exception e) {
            if (advice instanceof ThrowsAdvice) {
                ((ThrowsAdvice) advice).afterThrowing(targetClass, method, args, e);
            } else {
                throw new Throwable(e);
            }
        }
        return result;
    }

    public ProxyAdvisor(Advice advice, ProxyPointcut pointcut, int order) {
        this.advice = advice;
        this.pointcut = pointcut;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public ProxyPointcut getPointcut() {
        return pointcut;
    }

    public ProxyAdvisor() {
    }

    @Override
    public int compareTo(ProxyAdvisor o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}