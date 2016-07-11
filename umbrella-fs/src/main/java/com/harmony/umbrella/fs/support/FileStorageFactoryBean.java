package com.harmony.umbrella.fs.support;

import com.harmony.umbrella.fs.FileStorage;
import com.harmony.umbrella.fs.FileStorageFactory;
import com.harmony.umbrella.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
public class FileStorageFactoryBean implements FileStorageFactory {

	@Override
	public FileStorage getFileStorage(StorageType storageType) {
		return new ServerFileStorage();
	}

}
