package com.harmony.umbrella.fs.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.util.Date;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FileSystemStorageManager extends AbstractStorageManager {

    public static final String STORAGE_TYPE = "server";

    public static final String FILE_SYSTEM_ROOT_DIR = ApplicationMetadata.getOperatingSystemMetadata().userHome + ROOT_DIR;

    private static final String HOST;

    static {
        String host = null;
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (IOException e) {
        }
        HOST = host;
    }

    private File rootDir;

    public FileSystemStorageManager() {
        this(FILE_SYSTEM_ROOT_DIR);
    }

    public FileSystemStorageManager(String rootDir) {
        super(STORAGE_TYPE);
        this.rootDir = new File(rootDir);
    }

    @Override
    public StorageMetadata put(Resource resource, String name, Properties properties) throws IOException {
        if (properties == null) {
            properties = new Properties();
        }
        // XXX 扩展, 允许按不同的粒度来对parent进行切分
        String datePattern = TimeUtils.formatText(new Date(), "yyyyMMdd");
        File parent = new File(rootDir, datePattern);
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("can't access directory " + parent.getPath());
        }

        String destName = generateFileName(resource.getFile());
        File dest = new File(parent, destName);

        // start upload
        try (InputStream is = resource.getInputStream(); OutputStream os = new FileOutputStream(dest)) {
            FileStorageMetadata fsm = new FileStorageMetadata(storageType);
            fsm.contentLength = is.available();
            fsm.name = destName;
            fsm.path = datePattern;
            // 保留输入
            PropertiesUtils.apply(fsm.properties, properties);
            fsm.properties.put("server.rootDir", rootDir.getPath());
            fsm.properties.put("server.host", HOST);
            fsm.properties.put("server.uploadTime", System.currentTimeMillis());
            return fsm;
        }
    }

    @Override
    public Resource get(StorageMetadata sm) throws IOException {
        File file = new File(new File(rootDir, sm.getPath()), sm.getName());
        if (!file.isFile()) {
            throw new IOException("file not exists or not file " + sm);
        }
        return new FileSystemResource(file);
    }

    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

}
