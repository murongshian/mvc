package com.murongshian.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 类操作工具类
 */
public final class ClassUtil {

    /**
     * file形式url协议
     */
    public static final String FILE_PROTOCOL = "file";

    /**
     * jar形式url协议
     */
    public static final String JAR_PROTOCOL = "jar";

    /**
     * 获取classLoader
     *
     * @return 当前ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取Class
     *
     * @param className class全名
     * @return Class
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("load class error" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     *
     * @param className class全名
     * @param <T>       class的类型
     * @return 类的实例化
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return (T) clazz.newInstance();
        } catch (Exception e) {
            System.err.println("newInstance error" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     *
     * @param clazz Class
     * @param <T>   class的类型
     * @return 类的实例化
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            System.err.println("newInstance error" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性值
     *
     * @param field  属性
     * @param target 类实例
     * @param value  值
     */
    public static void setField(Field field, Object target, Object value) {
        setField(field, target, value, true);
    }

    /**
     * 设置类的属性值
     *
     * @param field      属性
     * @param target     类实例
     * @param value      值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        //当我们需要获取私有属性的属性值的时候，我们必须设置Accessible为true，然后才能获取
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            System.err.println("setField error" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取包下类集合
     *
     * @param basePackage 包的路径
     * @return 类集合
     */
    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL url = getClassLoader().getResource(basePackage.replace(".", "/"));
        if (null == url) {
            throw new RuntimeException("无法获取项目路径文件");
        }
        try {
            if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                File file = new File(url.getFile());
                Path basePath = file.toPath();

                Set<Class<?>> classSet = new HashSet<>();
                Stream<Path> pathStream = Files.walk(basePath);
                for (Iterator<Path> it = pathStream.iterator(); it.hasNext(); ) {
                    Path path = it.next();
                    if(path.toFile().getName().endsWith(".class")){
                        Class<?> clz = getClassByPath(path, basePath, basePackage);
                        classSet.add(clz);
                    }
                }
                return classSet;
//                //上述代码等效成下列lambda
//                return Files.walk(basePath)
//                        .filter(path -> path.toFile().getName().endsWith(".class"))
//                        .map(path -> getClassByPath(path, basePath, basePackage))
//                        .collect(Collectors.toSet());
            } else if (url.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)) {
                // 若在 jar 包中，则解析 jar 包中的 entry
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();

                Set<Class<?>> classSet = new HashSet<>();
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    if(jarEntry.getName().endsWith(".class")){
                        Class<?> clz = ClassUtil.getClassByJar(jarEntry);
                        classSet.add(clz);
                    }
                }
                return classSet;
//                //上述代码循环等代码等效成下列lambda
//                return jarURLConnection.getJarFile()
//                        .stream()
//                        .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
//                        .map(ClassUtil::getClassByJar)
//                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            System.err.println("load package error" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从Path获取Class
     *
     * @param classPath   类的路径
     * @param basePath    包目录的路径
     * @param basePackage 包名
     * @return 类
     */
    private static Class<?> getClassByPath(Path classPath, Path basePath, String basePackage) {
        String packageName = classPath.toString().replace(basePath.toString(), "");
        String className = (basePackage + packageName)
                .replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        // 如果class在根目录要去除最前面的.
        className = className.charAt(0) == '.' ? className.substring(1) : className;
        return loadClass(className);
    }

    /**
     * 从jar包获取Class
     *
     * @param jarEntry jar文件
     * @return 类
     */
    private static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryName = jarEntry.getName();
        // 获取类名
        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
        return loadClass(className);
    }
}