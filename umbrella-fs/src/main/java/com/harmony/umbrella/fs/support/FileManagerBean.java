//package com.harmony.umbrella.fs.support;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//
//import javax.ejb.EJB;
//import javax.ejb.EJBException;
//
//import com.harmony.umbrella.fs.FileItem;
//import com.harmony.umbrella.fs.FileStorage;
//import com.harmony.umbrella.fs.FileStorageFactory;
//import com.harmony.umbrella.fs.FileStorageMetadata;
//import com.harmony.umbrella.fs.StorageType;
//import com.harmony.umbrella.fs.eao.FileInfoEaoLocal;
//import com.harmony.umbrella.fs.entity.FileInfo;
//import com.huiju.module.data.eao.GenericEao;
//import com.huiju.module.data.logic.GenericLogicImpl;
//import com.huiju.module.util.Converter;
//
///**
// * @author wuxii@foxmail.com
// */
//public class FileManagerBean extends GenericLogicImpl<FileInfo, Long> implements FileInfoLocal, FileInfoRemote {
//
//	@EJB
//	private FileInfoEaoLocal fileInfoEao;
//
//	@EJB
//	private FileStorageFactory fileStorageFactory;
//
//	private Converter<FileStorageMetadata, FileInfo> converter = new ConverterToFileInfo();
//
//	private StorageType defaultStorageType = StorageType.SERVER;
//
//	@Override
//	protected GenericEao<FileInfo, Long> getGenericEao() {
//		return fileInfoEao;
//	}
//
//	@Override
//	public FileItem upload(File file, Long groupId) {
//		return null;
//	}
//
//	@Override
//	public FileItem upload(File file) {
//		return upload(file.getName(), file, defaultStorageType);
//	}
//
//	@Override
//	public FileItem upload(String fileName, File file) {
//		return upload(fileName, file, defaultStorageType);
//	}
//
//	@Override
//	public FileItem upload(String fileName, File file, StorageType storageType) {
//		FileStorage fileStorage = fileStorageFactory.getFileStorage(storageType);
//		try {
//			HashMap<String, Object> properties = new HashMap<String, Object>();
//			properties.put(FileStorage.METADATA_FILE_NAMEL, fileName);
//			FileStorageMetadata storageMetadata = fileStorage.putFile(file, properties);
//			FileInfo fileInfo = converter.convert(storageMetadata);
//			fileInfo = persist(fileInfo);
//			return new FileItemImpl(fileInfo, fileStorage, storageMetadata);
//		} catch (IOException e) {
//			throw new EJBException(e);
//		}
//	}
//
//	@Override
//	public FileItem upload(String fileName, InputStream in) {
//		return null;
//	}
//
//	@Override
//	public FileItem upload(String fileName, InputStream in, StorageType storageType) {
//		return null;
//	}
//
//	@Override
//	public FileItem download(Long fileId) {
//		FileInfo fileInfo = find(fileId);
//		return fileInfo != null ? convert(fileInfo) : null;
//	}
//
//	@Override
//	public void delete(Long... fileId) {
//	}
//
//	@Override
//	public void delete(FileItem... fileItem) {
//	}
//
//	@Override
//	public FileItem convert(FileInfo fileInfo) {
//		FileStorage fileStorage = fileStorageFactory.getFileStorage(fileInfo.getStorageType());
//		return new FileItemImpl(fileInfo, fileStorage, new ConverterToFileStorageMetadata());
//	}
//
//	public StorageType getDefaultStorageType() {
//		return defaultStorageType;
//	}
//
//	public void setDefaultStorageType(StorageType defaultStorageType) {
//		this.defaultStorageType = defaultStorageType;
//	}
//
//}
