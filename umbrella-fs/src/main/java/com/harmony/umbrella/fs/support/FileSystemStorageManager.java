package com.harmony.umbrella.fs.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;

import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.fs.StorageType;
import com.harmony.umbrella.io.FileSystemResource;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.IOUtils;

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
    public StorageMetadata putFile(File file) throws IOException {
        return putFile(file.getName(), file);
    }

    @Override
    public StorageMetadata putFile(String name, File file) throws IOException {
        return put(name, file.getAbsolutePath(), new FileInputStream(file), true);
    }

    @Override
    public StorageMetadata put(String name, InputStream is) throws IOException {
        return put(name, null, is, false);
    }

    private StorageMetadata put(Resource source, FileSystemResource dest) throws IOException {
        if (!source.getFile().isFile()) {
            throw new IOException(source + " is not file");
        }
        if (!dest.exists()) {
            FileUtils.createFile(dest.getFile());
        }
        FileStorageMetadata fsm = new FileStorageMetadata(source.getFilename(), source.getFile().getAbsolutePath(), storageType);

        InputStream is = source.getInputStream();
        OutputStream os = dest.getOutputStream();

        fsm.contentLength = is.available();
        fsm.name = dest.getFilename();
        fsm.path = dest.getFile().getAbsolutePath();
        fsm.properties.put("server.storageDirectory", storageDirectory);
        fsm.properties.put("server.host", HOST);

        IOUtils.copy(is, os);

        is.close();
        os.close();

        return fsm;
    }

    private StorageMetadata put(String name, String path, InputStream is, File storageFile, boolean close) throws IOException {
        FileStorageMetadata fsm = new FileStorageMetadata(name, path, storageType);

        FileOutputStream os = new FileOutputStream(storageFile);
        IOUtils.copy(is, os);
        try {
            os.close();
        } catch (IOException e) {
        }
        if (close) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        fsm.contentLength = is.available();
        fsm.name = storageFile.getName();
        fsm.path = storageFile.getAbsolutePath();
        fsm.properties.put("server.storageDirectory", storageDirectory);
        fsm.properties.put("server.host", HOST);

        return fsm;
    }

    private StorageMetadata put(String name, String path, InputStream is, boolean close) throws IOException {
        return null;
    }

}
