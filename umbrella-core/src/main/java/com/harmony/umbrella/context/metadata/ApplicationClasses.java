package com.harmony.umbrella.context.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourcePatternResolver;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationClasses {

    private static final Log log = Logs.getLog(ApplicationClasses.class);

    private static final Set<String> ROOT_PACKAGES = new HashSet<String>();

    @SuppressWarnings("rawtypes")
    private static final List<Class> classes = new ArrayList<Class>();

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private static boolean scanned = false;

    /**
     * 扫描应用配置的包下的所有class
     */
    @SuppressWarnings("rawtypes")
    public static void scan() {
        doInternal(new InternalAction() {

            @Override
            public void doAction() {
                log.info("scan package(s) {}", ROOT_PACKAGES);
                classes.clear();

                for (String pkg : ROOT_PACKAGES) {
                    String resourcePath = toResourcePath(pkg);
                    try {
                        Resource[] resources = resourcePatternResolver.getResources(resourcePath);
                        for (Resource resource : resources) {
                            Class<?> clazz = forClass(resource);
                            if (clazz != null && !classes.contains(clazz)) {
                                classes.add(clazz);
                            }
                        }
                    } catch (IOException e) {
                        log.error("{} package not found", pkg, e);
                    }
                }

                Collections.sort(classes, new Comparator<Class>() {
                    @Override
                    public int compare(Class o1, Class o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                scanned = true;

            }
        });

    }

    /**
     * 返回是否已经扫描标志
     * 
     * @return 是否已经扫描标准
     */
    public static boolean isScaned() {
        return scanned;
    }

    /**
     * 添加应用的包
     * 
     * @param pkgs
     *            包名
     * @return true 添加成功
     */
    public static void addApplicationPackage(final String... pkgs) {
        doInternal(new InternalAction() {

            @Override
            public void doAction() {
                for (String pkg : pkgs) {
                    addPackage(pkg);
                }
            }
        });

    }

    /**
     * 添加应用的包
     * 
     * @param pkg
     *            包
     * @return true 添加成功
     */
    public static void addApplicationPackage(Package pkg) {
        addApplicationPackage(pkg.getName());
    }

    private static boolean addPackage(String pkg) {
        if (isPackage(pkg)) {
            Iterator<String> it = ROOT_PACKAGES.iterator();
            while (it.hasNext()) {
                String p = it.next();
                if (pkg.startsWith(p)) {
                    // 已经存在父层的包
                    return false;
                } else if (p.startsWith(pkg)) {
                    // 输入的包是当前包的父层包
                    it.remove();
                }
            }
            ROOT_PACKAGES.add(pkg);
            return true;
        }
        return false;
    }

    /**
     * 将包名转化为需要扫描的路径
     * 
     * @param pkg
     *            包名
     * @return 扫描路径
     */
    private static String toResourcePath(String pkg) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pkg.replace(".", "/") + "/**/*.class";
    }

    /**
     * 采用正则表达式验证是否为包的格式
     * 
     * @param pkg
     *            包名
     * @return true 符合包名格式定义
     */
    public static boolean isPackage(String pkg) {
        if (pkg == null) {
            return false;
        }
        Package p = Package.getPackage(pkg);
        if (p != null) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");
        for (String token : pkg.split(".")) {
            if (!pattern.matcher(token).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从文件中读取类的信息
     * 
     * @param resource
     *            文件资源
     * @return 类信息
     */
    private static Class<?> forClass(Resource resource) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            return forName(new ClassReader(is).getClassName());
        } catch (IOException e) {
            log.error(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static Class<?> forName(String className) {
        try {
            return Class.forName(className.replace("/", "."), true, ClassUtils.getDefaultClassLoader());
        } catch (Error e) {
            log.warn("{} in classpath jar no fully configured, {}", className, e.toString());
        } catch (Throwable e) {
            log.error("{}", className, e);
        }
        return null;
    }

    /**
     * 扫描后包中所有的类
     * 
     * @return 扫描到的类
     */
    public static Class<?>[] getAllClasses() {
        if (!scanned) {
            scan();
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 按过滤器过滤扫描后的类,并返回过滤结果集
     * 
     * @param filter
     *            类过滤器
     * @return 过滤后的结果集
     */
    @SuppressWarnings("rawtypes")
    public static Class<?>[] getClasses(ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        for (Class clazz : getAllClasses()) {
            if (filter.accept(clazz)) {
                result.add(clazz);
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    /**
     * 过滤指定包下的类
     * 
     * @param pkg
     *            指定的包
     * @param filter
     *            类过滤器
     * @return 过滤后的结果集
     */
    @SuppressWarnings("rawtypes")
    public static Class<?>[] getClasses(String pkg, ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        if (containPackage(pkg)) {
            for (Class clazz : getAllClasses()) {
                if (clazz.getName().startsWith(pkg) && filter.accept(clazz)) {
                    result.add(clazz);
                }
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    private static boolean containPackage(String pkg) {
        if (pkg == null) {
            return false;
        }
        for (String p : ROOT_PACKAGES) {
            if (pkg.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    private static void doInternal(InternalAction action) {
        if (!scanned) {
            synchronized (ApplicationClasses.class) {
                if (!scanned) {
                    action.doAction();
                }
            }
        }
    }

    static interface InternalAction {

        void doAction();

    }

}
