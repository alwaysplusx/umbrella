package com.harmony.umbrella.fs.support;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.harmony.umbrella.fs.StorageMetadata;

/**
 * @author wuxii@foxmail.com
 */
public class AliyunStroageManager extends AbstractStorageManager {

    public static final String STORAGE_TYPE = "aliyun";

    public AliyunStroageManager() {
        super(STORAGE_TYPE);
    }

    @Override
    public StorageMetadata put(Resource resource, String name, Properties properties) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource get(StorageMetadata sm) throws IOException {
        throw new UnsupportedOperationException();
    }

}
