package com.harmony.umbrella.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MimeTypes {

    private static final Log log = Logs.getLog(MimeTypes.class);

    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>();

    static {
        Resource resource = ResourceManager.getInstance().getResource("mimeTypes.json");
        if (resource.exists()) {
            InputStream is = null;
            try {
                is = resource.getInputStream();
                Map<String, Object> mimeTypes = Json.toMap(IOUtils.toString(is));
                for (Entry<String, Object> entry : mimeTypes.entrySet()) {
                    Object value = entry.getValue();
                    if (value != null) {
                        MIME_TYPES.put(entry.getKey(), value.toString());
                    }
                }
            } catch (IOException e) {
                log.error("file not access", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        } else {
            log.warn("not customer mime types");
        }
    }

    /**
     * 根据扩展名加载mimeType, 没有对应的类型返回null
     * 
     * @param extension
     * @return
     */
    public static String getMimeType(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return MIME_TYPES.get(extension);
    }
}
