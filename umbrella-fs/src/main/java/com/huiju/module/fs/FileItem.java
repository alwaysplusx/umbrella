package com.huiju.module.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public interface FileItem extends Serializable {

    Long getFileId();

    String getFileName();

    StorageType getStorageType();

    String getExtension();

    InputStream getInputStream() throws IOException;

    File getFile() throws IOException;

}
