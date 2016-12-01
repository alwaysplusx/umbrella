package com.harmony.umbrella.fs.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.fs.StorageType;
import com.harmony.umbrella.io.FileSystemResource;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.WritableResource;
import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FileSystemStorageManager extends AbstractStorageManager {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_FILE_STORAGE_PATH = File.separator + STORAGE_PATH;

    private static final String HOST;

    static {
        String host = null;
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (IOException e) {
        }
        HOST = host;
    }

    private final String storageDirectory;

    public FileSystemStorageManager() {
        this(DEFAULT_FILE_STORAGE_PATH);
    }

    public FileSystemStorageManager(String storageDirectory) {
        super(StorageType.SERVER);
        this.storageDirectory = storageDirectory;
    }

    @Override
    public StorageMetadata put(String name, Resource resource, Properties properties) throws IOException {
        return put(name, resource, createWritableResource(), properties);
    }

    public WritableResource createWritableResource() throws IOException {
        // XXX 可扩展点, 服务器上的目录分割. 目标存储的文件名称
        File dir = new File(storageDirectory, TimeUtils.parseText(new Date(), "yyyyMMdd"));
        FileUtils.createDirectory(dir);
        return new FileSystemResource(new File(dir.getPath(), UUID.randomUUID().toString()));
    }

    public StorageMetadata put(String name, Resource source, WritableResource dest, Properties properties) throws IOException {
        if (!source.getFile().isFile()) {
            throw new IOException(source + " is not file");
        }
        if (!dest.exists()) {
            FileUtils.createFile(dest.getFile());
        }
        FileStorageMetadata fsm = new FileStorageMetadata(name, source.getFile().getAbsolutePath(), storageType);

        InputStream is = source.getInputStream();
        OutputStream os = dest.getOutputStream();

        fsm.contentLength = is.available();
        fsm.name = dest.getFilename();
        fsm.path = dest.getFile().getAbsolutePath();
        fsm.properties.putAll(properties);
        fsm.properties.put("server.storageDirectory", storageDirectory);
        fsm.properties.put("server.host", HOST);

        IOUtils.copy(is, os);

        is.close();
        os.close();

        return fsm;
    }

}
