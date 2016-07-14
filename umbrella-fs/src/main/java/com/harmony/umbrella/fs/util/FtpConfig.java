package com.harmony.umbrella.fs.util;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * @author wuxii@foxmail.com
 */
public class FtpConfig {

    public static final String ANONYMOUS_USERNAME = "anonymous";
    public static final String ANONYMOUS_PASSWORD = "anonymous@domain.com";

    private String username = ANONYMOUS_USERNAME;
    private String password = ANONYMOUS_PASSWORD;
    private String host;
    private int port = FTP.DEFAULT_PORT;

    private String workDirectory;

    private int fileType = FTP.BINARY_FILE_TYPE;

    public FtpConfig() {
    }

    public FtpConfig(String host) {
        this.host = host;
    }

    public FtpConfig(String host, String workDirectory, int fileType) {
        this.host = host;
        this.workDirectory = workDirectory;
        this.fileType = fileType;
    }

    public FTPClient createFTPClient() throws IOException {
        FTPClient ftp = new FTPClient();
        ftp.connect(host, port);
        if (!ftp.login(username, password)) {
            throw new IllegalStateException("wrong username or password");
        }
        ftp.setFileType(fileType);
        if (workDirectory != null) {
            ftp.changeWorkingDirectory(workDirectory);
        }
        return ftp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

}
