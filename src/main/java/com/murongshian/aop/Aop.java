package com.murongshian.aop;

import com.murongshian.aop.advice.Advice;
import com.murongshian.aop.annotation.Aspect;
import com.murongshian.aop.annotation.Order;
import com.murongshian.core.BeanContainer;

import java.util.*;

/**
 * Aop执行器
 */
public class Aop {

    /**
     * Bean容器
     */
    private BeanContainer beanContainer;

    public Aop() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行Aop
     */
    public void doAop() {
        //创建所有的代理通知列表
//        List<ProxyAdvisor> proxyList = beanContainer.getClassesBySuper(Advice.class)
//                .stream()
//                .filter(clz -> clz.isAnnotationPresent(Aspect.class))
//                .map(this::createProxyAdvisor)
//                .collect(Collectors.toList());
        //下列代码等效成上述lambda
        List<ProxyAdvisor> proxyAdvisorList = new ArrayList<>();//代理通知类的集合
        //获取Advice.class的子类或者实现类
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(Advice.class);
        for(Class<?> clz : classSet){
            if(clz.isAnnotationPresent(Aspect.class)){//检查类中是否标记了Aspect.class注解
                ProxyAdvisor proxyAdvisor = this.createProxyAdvisor(clz);//通过Aspect切面类创建代理通知类
                proxyAdvisorList.add(proxyAdvisor);
            }
        }

        //创建代理类并注入到Bean容器中
//        beanContainer.getClasses()
//                .stream()
//                .filter(clz -> !Advice.class.isAssignableFrom(clz))
//                .filter(clz -> !clz.isAnnotationPresent(Aspect.class))
//                .forEach(clz -> {
//                    List<ProxyAdvisor> matchProxies = createMatchProxies(proxyList, clz);
//                    if (matchProxies.size() > 0) {
//                        Object proxyBean = ProxyCreator.createProxy(clz, matchProxies);
//                        beanContainer.addBean(clz, proxyBean);
//                    }
//                });
        //下列代码等效成上述lambda
        Set<Class<?>> classes = beanContainer.getClasses();//获取“容器”中所有类
        for(Class<?> clz : classes){
            if(!Advice.class.isAssignableFrom(clz) && !clz.isAnnotationPresent(Aspect.class)){
                List<ProxyAdvisor> matchProxies = createMatchProxies(proxyAdvisorList, clz);
                if (matchProxies.size() > 0) {
                    Object proxyBean = ProxyCreator.createProxy(clz, matchProxies);//创建代理类
                    beanContainer.addBean(clz, proxyBean);//将代理类加入到“容器”中
                }
            }
        }
    }

    /**
     * 通过Aspect切面类创建代理通知类
     */
    private ProxyAdvisor createProxyAdvisor(Class<?> aspectClass) {
        int order = 0;
        if (aspectClass.isAnnotationPresent(Order.class)) {//aspectClass类是否有Order.class注解
            order = aspectClass.getAnnotation(Order.class).value();//获取该注解的值
        }
        String expression = aspectClass.getAnnotation(Aspect.class).pointcut();//获取Aspect.class注解的表达式
        ProxyPointcut proxyPointcut = new ProxyPointcut();//代理切点类
        proxyPointcut.setExpression(expression);//设置(AspectJ)表达式
        Advice advice = (Advice) beanContainer.getBean(aspectClass);//获取aspectClass实例
        return new ProxyAdvisor(advice, proxyPointcut, order);//代理通知类
    }

    /**
     * 获取目标类匹配的代理通知列表
     */
    private List<ProxyAdvisor> createMatchProxies(List<ProxyAdvisor> proxyList, Class<?> targetClass) {
        Object targetBean = beanContainer.getBean(targetClass);//获取目标类的实例对象
//        return proxyList
//                .stream()
//                .filter(advisor -> advisor.getPointcut().matches(targetBean.getClass()))
//                .sorted(Comparator.comparingInt(ProxyAdvisor::getOrder))
//                .collect(Collectors.toList());
        //下列代码等效成上述lambda
        List<ProxyAdvisor> proxyAdvisorList = new ArrayList<>();//代理通知类集合
        for(ProxyAdvisor proxyAdvisor : proxyList){
            if(proxyAdvisor.getPointcut().matches(targetBean.getClass())){//目标类是否和切点表达式匹配
                proxyAdvisorList.add(proxyAdvisor);
            }
        }
        Collections.sort(proxyAdvisorList);
        return proxyAdvisorList;
    }
}