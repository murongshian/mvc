package com.murongshian.mvc;

import java.util.Objects;

/**
 * PathInfo 存储http相关信息
 */
public class PathInfo {
    /**
     * http请求方法
     */
    private String httpMethod;

    /**
     * http请求路径
     */
    private String httpPath;

    public PathInfo() {}
    public PathInfo(String httpMethod, String httpPath) {
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathInfo pathInfo = (PathInfo) o;
        return httpMethod.equals(pathInfo.httpMethod) && httpPath.equals(pathInfo.httpPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, httpPath);
    }
}