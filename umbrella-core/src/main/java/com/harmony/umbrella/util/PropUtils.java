package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceLoader;
import com.harmony.umbrella.io.resource.DefaultResourceLoader;
import com.harmony.umbrella.io.resource.UrlResource;

/**
 * 属性加载工具
 *
 * @author wuxii@foxmail.com
 */
public class PropUtils {

    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    /**
     * 判断是否存在且是资源文件
     *
     * @param path
     *         文件路径
     */
    public static boolean exists(String path) {
        Resource resource = resourceLoader.getResource(path);
        return resource != null && resource.exists();
    }

    public static Properties loadProperties(URL url) throws IOException {
        return loadProperties(new UrlResource(url));
    }

    public static Properties loadProperties(Resource resource) throws IOException {
        return loadProperties(resource.getInputStream(), true);
    }

    public static Properties loadProperties(InputStream is) throws IOException {
        return loadProperties(is, false);
    }

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

    public static Properties loadProperties(File file) throws IOException {
        return loadProperties(new FileInputStream(file), true);
    }

    public static Properties loadProperties(Reader reader) throws IOException {
        return loadProperties(reader, false);
    }

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
     * 如果所对应的资源文件不存在忽略
     */
    public static Properties loadPropertiesSilently(String... paths) {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        for (String path : paths) {
            try {
                loadProperties(path, props);
            } catch (IOException e) {
                // ignore
            }
        }
        return props;
    }

    /**
     * 加载指定文件下的资源文件
     *
     * @param paths
     *         资源文件的路径
     * @return 资源文件中的属性
     * @throws IOException
     */
    public static Properties loadProperties(String... paths) throws IOException {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        for (String path : paths) {
            loadProperties(path, props);
        }
        return props;
    }

    private static void loadProperties(String path, Properties props) throws IOException {
        InputStream inStream = null;
        try {
            inStream = resourceLoader.getResource(path).getInputStream();
            props.load(inStream);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 从属性中过滤出前缀为指定值的属性集合
     *
     * @param prefix
     *         前缀
     * @param props
     *         待过滤的属性
     * @return 前缀符合要求的新属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props) {
        return filterStartWith(prefix, props, false);
    }

    /**
     * 在属性集合中过滤出前缀相同的属性集合
     *
     * @param prefix
     *         key的前缀
     * @param props
     *         属性集合
     * @param ignoreCase
     *         是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props, boolean ignoreCase) {
        Assert.notBlank(prefix, "prefix not allow null or blank");
        Properties result = new Properties();
        if (props == null || props.isEmpty()) {
            return result;
        }
        Set<String> propertyNames = props.stringPropertyNames();
        for (String name : propertyNames) {
            if (name.startsWith(prefix) || (ignoreCase && name.toLowerCase().startsWith(prefix.toLowerCase()))) {
                result.put(name, props.get(name));
            }
        }
        return result;
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *         key前缀
     * @param map
     *         属性集合
     * @return key前缀相同的属性集合
     */
    public static Map<String, Object> filterStartWith(String prefix, Map<String, Object> map) {
        return filterStartWith(prefix, map, false);
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *         key前缀
     * @param map
     *         属性集合
     * @param ignoreCase
     *         是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static Map<String, Object> filterStartWith(String prefix, Map<String, Object> map, boolean ignoreCase) {
        Assert.notBlank(prefix, "prefix not allow null or blank");
        Map<String, Object> result = new HashMap<String, Object>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (key.startsWith(prefix) || (ignoreCase && key.toLowerCase().startsWith(prefix.toLowerCase()))) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

}
