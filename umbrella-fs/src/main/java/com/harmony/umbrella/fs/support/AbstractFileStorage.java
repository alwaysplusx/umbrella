package com.harmony.umbrella.fs.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.fs.FileStorage;
import com.harmony.umbrella.fs.FileStorageMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.FileUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractFileStorage implements FileStorage {

	private static final long serialVersionUID = 1L;
	protected static final Log log = Logs.getLog(AbstractFileStorage.class);

	@Override
	public FileStorageMetadata putFile(File file) throws IOException {
		return putFile(file, new HashMap<String, Object>());
	}

	@Override
	public FileStorageMetadata putFile(File file, Map<String, Object> properties) throws IOException {
		properties.put(METADATA_ORIGINAL, file.getAbsolutePath());
		if (!properties.containsKey(METADATA_FILE_NAMEL)) {
			properties.put(METADATA_FILE_NAMEL, file.getName());
		}
		if (!properties.containsKey(METADATA_EXTENSION)) {
			properties.put(METADATA_EXTENSION, FileUtils.getExtension(file));
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return putFile(fis, properties);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	@Override
	public FileStorageMetadata putFile(String fileName, InputStream in) throws IOException {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(METADATA_FILE_NAMEL, fileName);
		properties.put(METADATA_EXTENSION, FileUtils.getExtension(fileName));
		return putFile(in, properties);
	}

	@Override
	public InputStream getInputStream(FileStorageMetadata metadata) throws IOException {
		return new FileInputStream(getFile(metadata));
	}

	@Override
	public void deleteFile(FileStorageMetadata metadata) throws IOException {
		File file = getFile(metadata);
		file.delete();
		log.info("delete file {}", file.getAbsoluteFile());
	}
}
