package com.harmony.umbrella.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 属性加载工具
 *
 * @author wuxii@foxmail.com
 */
@Slf4j
public class PropertiesUtils {

    /**
     * 加载指定文件下的资源文件
     *
     * @param paths 资源文件的路径
     * @return 资源文件中的属性
     * @throws IOException
     * @see {@link DefaultResourceLoader}
     */
    public static Properties loadProperties(String... paths) throws IOException {
        Properties props = new Properties();
        DefaultResourceLoader loader = new DefaultResourceLoader();
        for (String path : paths) {
            Resource resource = loader.getResource(path);
            InputStream is = resource.getInputStream();
            props.load(is);
            is.close();
        }
        return props;
    }

    /**
     * 从类路径下加载资源文件, 如果资源文件未找到则忽略异常
     *
     * @param paths 类路径下的资源文件
     * @return properties
     * @see DefaultResourceLoader
     */
    public static Properties loadPropertiesSilently(String... paths) {
        Properties props = new Properties();
        DefaultResourceLoader loader = new DefaultResourceLoader();
        for (String path : paths) {
            Resource resource = loader.getResource(path);
            try (InputStream is = resource.getInputStream()) {
                props.load(is);
            } catch (IOException e) {
                log.warn("{} property resource unload.", path, e);
            }
        }
        return props;
    }

    /**
     * 按路径加载资源文件
     *
     * @param url 资源文件所在位置
     * @return 加载后的资源
     * @throws IOException
     */
    public static Properties loadProperties(URL url) throws IOException {
        return loadProperties(new UrlResource(url));
    }

    /**
     * 按路径加载资源文件
     *
     * @param resource 资源所在位置
     * @return 加载后的资源
     * @throws IOException
     */
    public static Properties loadProperties(Resource resource) throws IOException {
        return loadProperties(resource.getInputStream(), true);
    }

    /**
     * 从输入流中获取资源文件内容
     *
     * @param is 输入流
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(InputStream is) throws IOException {
        return loadProperties(is, false);
    }

    /**
     * 加载文件资源
     *
     * @param file 资源文件
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(File file) throws IOException {
        return loadProperties(new FileInputStream(file), true);
    }

    /**
     * 加载文件资源
     *
     * @param reader 资源文件
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(Reader reader) throws IOException {
        return loadProperties(reader, false);
    }

    /**
     * 加载文件资源
     *
     * @param reader 资源文件
     * @param close  是否自动关闭reader
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(Reader reader, boolean close) throws IOException {
        Properties props = new Properties();
        try {
            props.load(reader);
        } finally {
            closeOrNot(reader, close);
        }
        return props;
    }

    /**
     * 从输入流中获取资源文件内容
     *
     * @param is    输入流
     * @param close 是否自动关闭流
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(InputStream is, boolean close) throws IOException {
        Properties props = new Properties();
        try {
            props.load(is);
        } finally {
            closeOrNot(is, close);
        }
        return props;
    }

    private static void closeOrNot(Closeable closeable, boolean close) {
        if (close) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.debug("close property failed, {}", closeable, e);
            }
        }
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix key前缀
     * @param map    属性集合
     * @return key前缀相同的属性集合
     */
    public static <T> Map<String, T> filterStartWith(String prefix, Map<String, T> map) {
        return filterStartWith(prefix, map, false);
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix     key前缀
     * @param map        属性集合
     * @param ignoreCase 是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static <T> Map<String, T> filterStartWith(String prefix, Map<String, T> map, boolean ignoreCase) {
        Assert.notNull(prefix, "prefix not allow null");
        Map<String, T> result = new LinkedHashMap<String, T>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (key.startsWith(prefix) || (ignoreCase && key.toLowerCase().startsWith(prefix.toLowerCase()))) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    /**
     * 过滤属性
     *
     * @param properties 带过来的属性
     * @param filter     entry filter
     * @return
     */
    public static <K, V> Map<K, V> filter(Map<K, V> properties, Predicate<Entry<K, V>> filter) {
        Map<K, V> result = new LinkedHashMap<>();
        Set<Entry<K, V>> entrySet = properties.entrySet();
        for (Entry<K, V> entry : entrySet) {
            if (filter.test(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 取left + rigth的并集, 覆盖left中已经存在的key
     *
     * @param left
     * @param right
     * @return
     */
    public static void apply(Map<?, ?> left, Map<?, ?> right) {
        apply(left, right, false);
    }

    /**
     * 取left + right的并集, 不覆盖left中已经存在的key
     *
     * @param left
     * @param right
     * @return
     */
    public static void applyIf(Map<?, ?> left, Map<?, ?> right) {
        apply(left, right, true);
    }

    private static void apply(Map l, Map r, boolean ignoreExists) {
        for (Object key : r.keySet()) {
            if (ignoreExists && l.containsKey(key)) {
                continue;
            }
            l.put(key, r.get(key));
        }
    }

}
