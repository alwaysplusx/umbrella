package com.huiju.module.fs.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.huiju.module.fs.FileMetadata;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.log.Log;
import com.huiju.module.log.Logs;
import com.huiju.module.util.FileUtils;
import com.huiju.module.util.IOUtils;
import com.huiju.module.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FileSystemFileStorage extends AbstractFileStorage {

    private static final long serialVersionUID = 1L;

    private String rootDirectory = ROOT_DIR;

    private static final Log log = Logs.getLog(FileSystemFileStorage.class);

    public FileSystemFileStorage() {
    }

    public FileSystemFileStorage(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public FileStorageMetadata putFile(FileMetadata fileMetadata, Map<String, Object> properties) throws IOException {
        DefaultFileStorageMetadata fsm = new DefaultFileStorageMetadata(getStorageType());
        fsm.setFileName(fileMetadata.getFileName());
        fsm.setOriginal(fileMetadata.getAbsolutePath());

        String destinationFileName = getAndCreateUploadDirectory() + createDestinationFileName(fsm.getFileName());
        File destinationFile = new File(destinationFileName);
        if (destinationFile.exists()) {
            throw new IOException("destination file already exists");
        }

        InputStream is = fileMetadata.getInputStream();
        fsm.setContentLength(is.available());
        fsm.setDestination(destinationFile.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(destinationFile);
        IOUtils.copy(is, fos);
        fos.close();
        is.close();

        return fsm;
    }

    @Override
    public File getFile(FileStorageMetadata metadata) throws IOException {
        File file = null;
        String path = metadata.getDestination();
        if (path == null || !(file = new File(path)).isFile()) {
            throw new IOException("file not fonud " + path);
        }
        return file;
    }

    public String getAndCreateUploadDirectory() throws IOException {
        StringBuilder path = new StringBuilder(rootDirectory);
        if (!rootDirectory.endsWith(File.separator)) {
            path.append(File.separator);
        }
        path.append(TimeUtils.parseText(new Date(), "yyyyMMdd"));
        path.append(File.separator);
        // create upload directory
        FileUtils.createDirectory(path.toString());
        return path.toString();
    }

    @Override
    public void deleteFile(FileStorageMetadata metadata) throws IOException {
        File file = getFile(metadata);
        file.delete();
        log.info("delete file {}", file.getAbsoluteFile());
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.SERVER;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

}
