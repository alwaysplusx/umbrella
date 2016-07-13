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

    @SuppressWarnings("rawtypes")
    public static synchronized void scan() {
        if (!scanned) {
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
    }

    public static synchronized void addApplicationPackage(Package pkg) {
        addPkg(pkg.getName());
    }

    public static synchronized void addApplicationPackage(String... pkgs) {
        for (String pkg : pkgs) {
            if (isPackage(pkg)) {
                addPkg(pkg);
            }
        }
    }

    private static void addPkg(String pkg) {
        if (!scanned) {
            Iterator<String> it = ROOT_PACKAGES.iterator();
            while (it.hasNext()) {
                String p = it.next();
                if (pkg.startsWith(p)) {
                    // 已经存在父层的包
                    return;
                } else if (p.startsWith(pkg)) {
                    // 输入的包是当前包的父层包
                    it.remove();
                    break;
                }
            }
            ROOT_PACKAGES.add(pkg);
        }
    }

    private static String toResourcePath(String pkg) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pkg.replace(".", "/") + "/**/*.class";
    }

    public static boolean isPackage(String pkg) {
        Package p = Package.getPackage(pkg);
        if (p != null) {
            return true;
        }
        return Pattern.compile("^([a-zA-Z]+[.][a-zA-Z]+)[.]*.*").matcher(pkg).matches();
    }

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

    public static Class<?>[] getAllClasses() {
        return classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    public static Class<?>[] getClasses(ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        for (Class clazz : classes) {
            if (filter.accept(clazz)) {
                result.add(clazz);
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    @SuppressWarnings("rawtypes")
    public static Class<?>[] getClasses(String pkg, ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        if (containPackage(pkg)) {
            for (Class clazz : classes) {
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

}
