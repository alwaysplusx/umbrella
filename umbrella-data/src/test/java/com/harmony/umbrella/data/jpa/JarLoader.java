package com.harmony.umbrella.data.jpa;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.harmony.umbrella.log.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public class JarLoader {

    private final ClassLoader classLoader;

    private PathMatcher matcher = new AntPathMatcher();

    private Set<URL> jarUrls;

    public JarLoader() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public JarLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Set<URL> findMatchJarUrls(String... patterns) {
        Set<URL> result = new LinkedHashSet<>();
        URL[] jarUrls = getAllJarUrls();
        for (URL url : jarUrls) {
            String path = url.getPath();
            for (String pattern : patterns) {
                if (result.contains(url) //
                        || matcher.match(pattern, path) //
                        || matcher.match(pattern + ResourceUtils.JAR_URL_SEPARATOR, path)) {
                    result.add(url);
                    continue;
                }
            }
        }
        return result;
    }

    public URL[] getAllJarUrls() {
        if (jarUrls == null) {
            jarUrls = scanAllJars();
        }
        return jarUrls.toArray(new URL[jarUrls.size()]);
    }

    private Set<URL> scanAllJars() {
        Set<URL> result = new LinkedHashSet<>();
        addAllJarUrls(classLoader, result);
        return result;
    }

    private void addAllJarUrls(ClassLoader loader, Set<URL> result) {
        if (loader instanceof URLClassLoader) {
            for (URL url : ((URLClassLoader) loader).getURLs()) {
                if (isJarUrl(url)) {
                    result.add(formatJarUrl(url));
                }
            }
        }

        if (loader == ClassLoader.getSystemClassLoader()) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries(result);
        }

        if (loader != null) {
            try {
                addAllJarUrls(loader.getParent(), result);
            } catch (Exception ex) {
            }
        }
    }

    private void addClassPathManifestEntries(Set<URL> result) {
        String javaClassPathProperty = System.getProperty("java.class.path");
        String[] paths = StringUtils.delimitedListToStringArray(javaClassPathProperty, System.getProperty("path.separator"));
        for (String path : paths) {
            URL url = parseToJarUrl(path);
            if (url != null) {
                result.add(url);
            }
        }
    }

    private URL parseToJarUrl(String path) {
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        try {
            URL url = new URL("file:" + (absolutePath.startsWith("/") ? "" : "/") + absolutePath);
            if (isJarUrl(url)) {
                return formatJarUrl(url);
            }
        } catch (MalformedURLException e) {
        }
        return null;
    }

    private URL formatJarUrl(URL url) {
        try {
            return new URL(ResourceUtils.JAR_URL_PREFIX + url.toString() + ResourceUtils.JAR_URL_SEPARATOR);
        } catch (MalformedURLException e) {
        }
        return null;
    }

    private boolean isJarUrl(URL url) {
        return ResourceUtils.isJarFileURL(url) || ResourceUtils.isJarURL(url);
    }

    public static Set<URL> findJars(String... patterns) {
        JarLoader loader = new JarLoader();
        Set<URL> result = new LinkedHashSet<>();
        for (String pattern : patterns) {
            Set<URL> jarUrls = loader.findMatchJarUrls(pattern);
            StaticLogger.debug("found %s jars with pattern [%s]", jarUrls.size(), pattern);
            result.addAll(jarUrls);
        }
        return result;
    }

}
