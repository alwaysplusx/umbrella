package com.huiju.module.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.huiju.module.util.FileUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FileMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    private File file;
    private String fileName;
    private String extension;

    public FileMetadata(File file) {
        this(file.getName(), file);
    }

    public FileMetadata(String fileName, File file) {
        this.file = file;
    }

    public String getFileName() {
        if (fileName == null && file != null) {
            fileName = file.getName();
        }
        return fileName;
    }

    public String getExtension() {
        if (extension == null && file != null) {
            extension = (fileName != null) ? FileUtils.getExtension(fileName) : (file != null) ? FileUtils.getExtension(file) : null;
        }
        return extension;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

}
