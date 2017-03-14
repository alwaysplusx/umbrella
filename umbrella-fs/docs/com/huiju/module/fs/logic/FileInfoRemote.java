package com.huiju.module.fs.logic;

import java.io.File;
import java.io.IOException;

import javax.ejb.Remote;

import com.huiju.module.data.logic.GenericLogic;
import com.huiju.module.fs.FileItem;
import com.huiju.module.fs.FileManager;
import com.huiju.module.fs.entity.FileInfo;

/**
 * @author wuxii@foxmail.com
 */
@Remote
public interface FileInfoRemote extends GenericLogic<FileInfo, Long>, FileManager {

    FileItem upload(File file, Long groupId) throws IOException;

    FileItem upload(String fileName, File file, Long groupId) throws IOException;

    FileItem convert(FileInfo fileInfo);

    void group(Long... items);

    void group(FileItem... items);
}
