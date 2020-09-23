package com.murongshian.ioc;

import com.murongshian.ioc.annotation.Autowired;
import com.murongshian.core.BeanContainer;
import com.murongshian.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.Set;

public class Ioc {

    /**
     * Bean容器
     */
    private BeanContainer beanContainer;

    public Ioc() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行Ioc
     */
    public void doIoc() {
        for (Class<?> clz : beanContainer.getClasses()) { //遍历Bean容器中所有的Bean类
            final Object targetBean = beanContainer.getBean(clz);//获取某一个bean的实例
            //返回 Field 对象的一个数组，该数组包含此 Class 对象所表示的类或接口所声明的所有字段（包括私有成员）
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) { //遍历Bean中的所有属性
                if (field.isAnnotationPresent(Autowired.class)) {// 如果该属性含有Autowired注解，则对其注入
                    final Class<?> fieldClass = field.getType();//该属性的类型
                    Object fieldValue = getClassInstance(fieldClass);//根据Class获取其实例或者实现类
                    if (null == fieldValue) {
                        throw new RuntimeException("无法注入对应的类，目标类型:" + fieldClass.getName());
                    } else {
                        ClassUtil.setField(field, targetBean, fieldValue);
                    }
                }
            }
        }
    }

    /**
     * 根据Class获取其实例或者实现类
     */
    private Object getClassInstance(final Class<?> clz) {
        Object bean = beanContainer.getBean(clz);//获取clz的实例对象
        if(bean == null){//如果实例对象为空
            Class<?> implementClass = getImplementClass(clz);//可能是个接口，从而获取它的实现类
            if (null == implementClass) {
                return null;
            }
            return beanContainer.getBean(implementClass);//获取实现类的对象
        }else{
            return bean;
        }
//        //上述代码等效成下列lambda
//        return Optional.ofNullable(beanContainer.getBean(clz))
//                .orElseGet(() -> {
//                    Class<?> implementClass = getImplementClass(clz);
//                    if (null != implementClass) {
//                        return beanContainer.getBean(implementClass);
//                    }
//                    return null;
//                });
    }

    /**
     * 获取接口的实现类
     */
    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(interfaceClass);//获取接口的实现类
        if(classSet.isEmpty()){//如果为空
            return null;//返回空
        }else{
            return classSet.iterator().next();//返回第一个实现类
        }
        //上述代码等效成下列lambda
//        return beanContainer.getClassesBySuper(interfaceClass)
//                .stream()
//                .findFirst()
//                .orElse(null);
    }
}