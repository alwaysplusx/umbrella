package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public interface FileMetadata extends Serializable {

    public String getFileName();

    public String getExtension();

    public String getAbsolutePath();

    public File getFile();

    public InputStream getInputStream() throws IOException;

}
