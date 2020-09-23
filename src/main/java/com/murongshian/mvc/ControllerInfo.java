package com.murongshian.mvc;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ControllerInfo 存储Controller相关信息
 */
public class ControllerInfo {
    /**
     * controller类
     */
    private Class<?> controllerClass;

    /**
     * 执行的方法
     */
    private Method invokeMethod;

    /**
     * 方法参数别名对应参数类型
     */
    private Map<String, Class<?>> methodParameter;

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getInvokeMethod() {
        return invokeMethod;
    }

    public Map<String, Class<?>> getMethodParameter() {
        return methodParameter;
    }

    public ControllerInfo(Class<?> controllerClass, Method invokeMethod, Map<String, Class<?>> methodParameter) {
        this.controllerClass = controllerClass;
        this.invokeMethod = invokeMethod;
        this.methodParameter = methodParameter;
    }

    public ControllerInfo() {
    }
}