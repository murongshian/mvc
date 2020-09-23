package com.murongshian.mvc.handler;

import com.murongshian.core.BeanContainer;
import com.murongshian.mvc.ControllerInfo;
import com.murongshian.mvc.PathInfo;
import com.murongshian.mvc.RequestHandlerChain;
import com.murongshian.mvc.annotation.RequestMapping;
import com.murongshian.mvc.annotation.RequestParam;
import com.murongshian.mvc.annotation.ResponseBody;
import com.murongshian.mvc.render.JsonRender;
import com.murongshian.mvc.render.NotFoundRender;
import com.murongshian.mvc.render.Render;
import com.murongshian.mvc.render.ViewRender;
import com.murongshian.util.CastUtil;
import com.murongshian.util.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller请求处理
 */
public class ControllerHandler implements Handler {
    /**
     * 请求信息和controller信息关系map
     */
    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();
    /**
     * bean容器
     */
    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();

        Set<Class<?>> mappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        this.initPathControllerMap(mappingSet);
    }

    @Override
    public boolean handle(final RequestHandlerChain handlerChain) {
        String method = handlerChain.getRequestMethod();
        String path = handlerChain.getRequestPath();
        ControllerInfo controllerInfo = pathControllerMap.get(new PathInfo(method, path));
        if (null == controllerInfo) {
            handlerChain.setRender(new NotFoundRender());
            return false;
        }
        Object result = invokeController(controllerInfo, handlerChain.getRequest());
        setRender(result, controllerInfo, handlerChain);
        return true;
    }

    /**
     * 执行controller方法
     */
    private Object invokeController(ControllerInfo controllerInfo, HttpServletRequest request) {
        Map<String, String> requestParams = getRequestParams(request);
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParams);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 设置请求结果执行器
     */
    private void setRender(Object result, ControllerInfo controllerInfo, RequestHandlerChain handlerChain) {
        if (null == result) {
            return;
        }
        Render render;
        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            render = new JsonRender(result);
        } else {
            render = new ViewRender(result);
        }
        handlerChain.setRender(render);
    }

    /**
     * 初始化pathControllerMap
     */
    private void initPathControllerMap(Set<Class<?>> mappingSet) {
        mappingSet.forEach(this::addPathController);
    }

    /**
     * 添加controllerInfo到pathControllerMap中
     */
    private void addPathController(Class<?> clz) {
        RequestMapping requestMapping = clz.getAnnotation(RequestMapping.class);
        String basePath = requestMapping.value();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                if (!methodPath.startsWith("/") && !"/".equals(basePath)) {
                    methodPath = "/" + methodPath;
                }
                String url = basePath + methodPath;
                Map<String, Class<?>> methodParams = this.getMethodParams(method);
                String httpMethod = String.valueOf(methodRequest.method());
                PathInfo pathInfo = new PathInfo(httpMethod, url);
                if (pathControllerMap.containsKey(pathInfo)) {
                    System.out.println("url: " + pathInfo.getHttpPath() + "重复注册");
                }
                ControllerInfo controllerInfo = new ControllerInfo(clz, method, methodParams);
                this.pathControllerMap.put(pathInfo, controllerInfo);
                System.out.println("mapped:,method= "+pathInfo.getHttpPath() + pathInfo.getHttpMethod()  +" controller: " +
                         controllerInfo.getControllerClass().getName() + controllerInfo.getInvokeMethod().getName());
            }
        }
    }

    /**
     * 获取执行方法的参数
     */
    private Map<String, Class<?>> getMethodParams(Method method) {
        Map<String, Class<?>> map = new HashMap<>();
        for (Parameter parameter : method.getParameters()) {
            RequestParam param = parameter.getAnnotation(RequestParam.class);
            // TODO: 不使用注解匹配参数名字
            if (null == param) {
                throw new RuntimeException("必须有RequestParam指定的参数名");
            }
            map.put(param.value(), parameter.getType());
        }
        return map;
    }

    /**
     * 获取HttpServletRequest中的参数
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        Map<String, String[]> param = request.getParameterMap();
        for (Map.Entry<String, String[]> itemList : param.entrySet()) {
            if(ValidateUtil.isNotEmpty(itemList.getValue())){
                paramMap.put(itemList.getKey(), itemList.getValue()[0]);
            }
        }
//        //上述代码等效成下列lambda
//        //GET和POST方法是这样获取请求参数的
//        request.getParameterMap().forEach((paramName, paramsValues) -> {
//            if (ValidateUtil.isNotEmpty(paramsValues)) {
//                paramMap.put(paramName, paramsValues[0]);
//            }
//        });
        // TODO: Body、Path、Header等方式的请求参数获取
        return paramMap;
    }

    /**
     * 实例化方法参数
     */
    private List<Object> instantiateMethodArgs(Map<String, Class<?>> methodParams, Map<String, String> requestParams) {
        List<Object> objectList = new ArrayList<>();
        Set<String> keySet = methodParams.keySet();
        for(String paramName : keySet){
            Class<?> type = methodParams.get(paramName);
            String requestValue = requestParams.get(paramName);
            Object value;
            if(null == requestValue){
                value = CastUtil.primitiveNull(type);
            }else{
                value = CastUtil.convert(type,requestValue);
            }
            objectList.add(value);
        }
        return objectList;
        //上述代码等效成下列lambda
//        return methodParams.keySet().stream().map(paramName -> {
//            Class<?> type = methodParams.get(paramName);
//            String requestValue = requestParams.get(paramName);
//            Object value;
//            if (null == requestValue) {
//                value = CastUtil.primitiveNull(type);
//            } else {
//                value = CastUtil.convert(type, requestValue);
//                // TODO: 实现非原生类的参数实例化
//            }
//            return value;
//        }).collect(Collectors.toList());
    }
}