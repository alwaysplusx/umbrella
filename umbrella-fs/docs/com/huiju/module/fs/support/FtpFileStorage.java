package com.huiju.module.fs.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;

import com.huiju.module.fs.FileMetadata;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.fs.util.FtpConfig;
import com.huiju.module.fs.util.FtpUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.module.util.TimeUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class FtpFileStorage extends AbstractFileStorage {

    private static final long serialVersionUID = 1L;

    private String rootDirectory = ROOT_DIR;

    private FtpConfig ftpConfig;

    public FtpFileStorage() {
    }

    public FtpFileStorage(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    @Override
    public FileStorageMetadata putFile(FileMetadata fileMetadata, Map<String, Object> properties) throws IOException {
        FTPClient ftpClient = ftpConfig.createFTPClient();

        DefaultFileStorageMetadata fsm = new DefaultFileStorageMetadata(getStorageType());
        fsm.setFileName(fileMetadata.getFileName());
        fsm.setOriginal(fileMetadata.getAbsolutePath());

        InputStream is = fileMetadata.getInputStream();
        fsm.setContentLength(is.available());

        String destinationFileName = getAndCreateUploadDirectory(ftpClient) + createDestinationFileName(fsm.getFileName());
        fsm.setDestination(destinationFileName);

        if (!ftpClient.storeFile(destinationFileName, is)) {
            throw new IOException("destination file not store");
        }

        fsm.addProperty("ftp.address", ftpClient.getRemoteAddress().getHostAddress());
        fsm.addProperty("ftp.port", ftpClient.getRemotePort());
        fsm.addProperty("ftp.workDirectory", ftpClient.printWorkingDirectory());

        // 断开连接
        ftpClient.disconnect();
        is.close();

        return fsm;
    }

    @Override
    public File getFile(FileStorageMetadata metadata) throws IOException {
        FTPClient ftpClient = ftpConfig.createFTPClient();
        try {
            Map<String, Object> properties = metadata.getProperties();
            String workDirectory = (String) properties.get("ftp.workDirectory");
            if (StringUtils.isNotBlank(workDirectory)) {
                ftpClient.changeWorkingDirectory(workDirectory);
            }
            return FtpUtils.getFile(ftpClient, metadata.getDestination());
        } finally {
            ftpClient.disconnect();
        }
    }

    @Override
    public void deleteFile(FileStorageMetadata metadata) throws IOException {
        FTPClient ftpClient = ftpConfig.createFTPClient();
        ftpClient.deleteFile(metadata.getDestination());
        ftpClient.disconnect();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.FTP;
    }

    private String getAndCreateUploadDirectory(FTPClient ftpClient) throws IOException {
        StringBuilder path = new StringBuilder(rootDirectory);
        if (!rootDirectory.endsWith(File.separator)) {
            path.append(File.separator);
        }
        path.append(TimeUtils.parseText(new Date(), "yyyyMMdd"));
        path.append(File.separator);
        // create upload directory
        FtpUtils.mkdirs(ftpClient, path.toString());
        return path.toString();
    }

    public String getAndCreateUploadDirectory() throws IOException {
        return getAndCreateUploadDirectory(ftpConfig.createFTPClient());
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public FtpConfig getFtpConfig() {
        return ftpConfig;
    }

    public void setFtpConfig(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

}
