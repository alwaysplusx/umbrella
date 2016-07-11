package com.harmony.umbrella.fs;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public interface FileStorageMetadata extends Serializable {

    String getFileName();

    String getOriginal();

    String getDestination();

    Long getContentLength();

    StorageType getStorageType();

    Map<String, Object> getProperties();

}
