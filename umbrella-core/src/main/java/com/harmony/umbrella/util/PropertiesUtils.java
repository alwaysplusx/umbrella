package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

/**
 * 属性加载工具
 *
 * @author wuxii@foxmail.com
 */
public class PropertiesUtils {

    /**
     * 加载指定文件下的资源文件
     *
     * @param paths
     *            资源文件的路径
     * @return 资源文件中的属性
     * @throws IOException
     * @see {@link DefaultResourceLoader}
     */
    public static Properties loadProperties(String... paths) throws IOException {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
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
     * @param paths
     *            类路径下的资源文件
     * @return properties
     * @see DefaultResourceLoader
     */
    public static Properties loadPropertiesSilently(String... paths) {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        DefaultResourceLoader loader = new DefaultResourceLoader();
        for (String path : paths) {
            Resource resource = loader.getResource(path);
            InputStream is;
            try {
                is = resource.getInputStream();
                props.load(is);
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return props;
    }

    /**
     * 按路径加载资源文件
     * 
     * @param url
     *            资源文件所在位置
     * @return 加载后的资源
     * @throws IOException
     */
    public static Properties loadProperties(URL url) throws IOException {
        return loadProperties(new UrlResource(url));
    }

    /**
     * 按路径加载资源文件
     * 
     * @param resource
     *            资源所在位置
     * @return 加载后的资源
     * @throws IOException
     */
    public static Properties loadProperties(Resource resource) throws IOException {
        return loadProperties(resource.getInputStream(), true);
    }

    /**
     * 从输入流中获取资源文件内容
     * 
     * @param is
     *            输入流
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(InputStream is) throws IOException {
        return loadProperties(is, false);
    }

    /**
     * 从输入流中获取资源文件内容
     * 
     * @param is
     *            输入流
     * @param close
     *            是否自动关闭流
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(InputStream is, boolean close) throws IOException {
        Properties props = new Properties();
        props.load(is);
        if (close) {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    /**
     * 加载文件资源
     * 
     * @param file
     *            资源文件
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(File file) throws IOException {
        return loadProperties(new FileInputStream(file), true);
    }

    /**
     * 加载文件资源
     * 
     * @param reader
     *            资源文件
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(Reader reader) throws IOException {
        return loadProperties(reader, false);
    }

    /**
     * 加载文件资源
     * 
     * @param reader
     *            资源文件
     * @param close
     *            是否自动关闭reader
     * @return 资源内容
     * @throws IOException
     */
    public static Properties loadProperties(Reader reader, boolean close) throws IOException {
        Properties props = new Properties();
        try {
            props.load(reader);
        } catch (Exception e) {
            if (close) {
                try {
                    reader.close();
                } catch (Exception e1) {
                }
            }
        }
        return props;
    }

    /**
     * 从属性中过滤出前缀为指定值的属性集合
     *
     * @param prefix
     *            前缀
     * @param props
     *            待过滤的属性
     * @return 前缀符合要求的新属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props) {
        return filterStartWith(prefix, props, false);
    }

    /**
     * 在属性集合中过滤出前缀相同的属性集合
     *
     * @param prefix
     *            key的前缀
     * @param props
     *            属性集合
     * @param ignoreCase
     *            是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props, boolean ignoreCase) {
        Assert.hasLength(prefix, "prefix not allow null or blank");
        Properties result = new Properties();
        result.putAll(filterStartWith(prefix, (Map) props, ignoreCase));
        return result;
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *            key前缀
     * @param map
     *            属性集合
     * @return key前缀相同的属性集合
     */
    public static <T> Map<String, T> filterStartWith(String prefix, Map<String, T> map) {
        return filterStartWith(prefix, map, false);
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *            key前缀
     * @param map
     *            属性集合
     * @param ignoreCase
     *            是否忽略大小写
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
     * @param properties
     *            带过来的属性
     * @param entryFilter
     *            entry filter
     * @return
     */
    public static Map filter(Map properties, EntryFilter entryFilter) {
        Map result = new LinkedHashMap<>();
        Set<Entry> entrySet = properties.entrySet();
        for (Entry entry : entrySet) {
            if (entryFilter.accept(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 合并properties(返回值为一个新的map对象)
     * 
     * @param properties
     *            属性组
     * @return 合并后的属性
     */
    public static Map mergeProperties(Map... properties) {
        Map<Object, Object> result = new HashMap<>();
        for (Map map : properties) {
            if (map != null) {
                result.putAll(map);
            }
        }
        return result;
    }

    public static Map apply(Map l, Map r) {
        return apply(l, r, false);
    }

    public static Map applyIf(Map l, Map r) {
        return apply(l, r, true);
    }

    public static Map apply(Map l, Map r, boolean ignoreExists) {
        for (Object k : r.keySet()) {
            if (ignoreExists && l.containsKey(k)) {
                continue;
            }
            l.put(k, r.get(k));
        }
        return l;
    }

    /**
     * 属性entry过滤器
     * 
     * @author wuxii@foxmail.com
     */
    public interface EntryFilter {

        /**
         * 属性过滤
         * 
         * @param entry
         *            属性entry
         * @return true or false
         */
        boolean accept(Map.Entry<Object, Object> entry);

    }

}
