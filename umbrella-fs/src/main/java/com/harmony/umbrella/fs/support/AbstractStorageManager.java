package com.harmony.umbrella.fs.support;

import com.harmony.umbrella.fs.StorageManager;
import com.harmony.umbrella.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractStorageManager implements StorageManager {

    private static final long serialVersionUID = 1L;

    protected final StorageType storageType;

    public AbstractStorageManager(StorageType storageType) {
        this.storageType = storageType;
    }

    @Override
    public final StorageType getStorageType() {
        return storageType;
    }
}
