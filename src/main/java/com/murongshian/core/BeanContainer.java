package com.murongshian.core;

import com.murongshian.aop.annotation.Aspect;
import com.murongshian.core.annotation.Component;
import com.murongshian.core.annotation.Controller;
import com.murongshian.core.annotation.Repository;
import com.murongshian.core.annotation.Service;
import com.murongshian.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean容器
 */
public class BeanContainer {
    /**
     * 存放所有Bean的Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 是否加载Bean
     */
    private boolean isLoadBean = false;

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class, Aspect.class);

    /**
     * 获取Bean容器实例
     *
     * @return BeanContainer
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    /**
     * 扫描加载所有Bean
     *
     * @param basePackage 包名
     */
    public void loadBeans(String basePackage) {
        if (isLoadBean()) {//如果bean已经加载了就不需要加载了
            System.out.println("bean已经加载");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);//获取包下类的集合
        for(Class<?> clz : classSet){//将拥有BEAN_ANNOTATION中注解的类和类的实例放进beanMap中(加载bean)
            for(Class<? extends Annotation> annotation : BEAN_ANNOTATION){
                if(clz.isAnnotationPresent(annotation)){
                    beanMap.put(clz,ClassUtil.newInstance(clz));
                }
            }
        }
//        //上述双重for循环等效于下列lambda
//        classSet.stream().filter(clz -> {
//            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
//                if (clz.isAnnotationPresent(annotation)) {
//                    return true;
//                }
//            }
//            return false;
//        }).forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
        isLoadBean = true;
    }

    /**
     * 是否加载Bean
     *
     * @return 是否加载
     */
    public boolean isLoadBean() {
        return isLoadBean;
    }

    /**
     * 获取Bean实例
     *
     * @param clz Class类型
     * @return Bean实例
     */
    public Object getBean(Class<?> clz) {
        if (null == clz) {
            return null;
        }
        return beanMap.get(clz);
    }

    /**
     * 获取所有Bean集合
     *
     * @return Bean集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 添加一个Bean实例
     *
     * @param clz  Class类型
     * @param bean Bean实例
     */
    public void addBean(Class<?> clz, Object bean) {
        beanMap.put(clz, bean);
    }

    /**
     * 移除一个Bean实例
     *
     * @param clz Class类型
     */
    public void removeBean(Class<?> clz) {
        beanMap.remove(clz);
    }

    /**
     * Bean实例数量
     *
     * @return 数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 所有Bean的Class集合
     *
     * @return Class集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 通过注解获取Bean的Class集合
     *
     * @param annotation 注解
     * @return Class集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> classSet = new HashSet<>();
        Set<Class<?>> keySet = beanMap.keySet();
        for(Class<?> key : keySet){
            if(key.isAnnotationPresent(annotation)){
                classSet.add(key);
            }
        }
        return classSet;
        //上述代码等效成下列lambda
//        return beanMap.keySet()
//                .stream()
//                .filter(clz -> clz.isAnnotationPresent(annotation))
//                .collect(Collectors.toSet());
    }

    /**
     * 通过实现类或者父类获取Bean的Class集合
     *
     * @param interfaceClass 接口Class或者父类Class
     * @return Class集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceClass) {
        Set<Class<?>> classSet = new HashSet<>();
        Set<Class<?>> keySet = beanMap.keySet();
        for(Class<?> key : keySet){
            //interfaceClass是key的父类或者接口，但不是同一个类或者接口
            if(interfaceClass.isAssignableFrom(key) && !key.equals(interfaceClass)){
                classSet.add(key);
            }
        }
        return classSet;
//        //上述代码功能等效成下列lambda
//        return beanMap.keySet()
//                .stream()
//                .filter(interfaceClass::isAssignableFrom)
//                .filter(clz -> !clz.equals(interfaceClass))
//                .collect(Collectors.toSet());
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;
        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    private BeanContainer(){}
}