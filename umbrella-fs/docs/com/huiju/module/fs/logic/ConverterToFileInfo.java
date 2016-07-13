package com.huiju.module.fs.logic;

import java.io.Serializable;
import java.util.Calendar;

import com.huiju.module.context.UserContext;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.entity.FileInfo;
import com.huiju.module.json.Json;
import com.huiju.module.util.Converter;
import com.huiju.module.util.FileUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConverterToFileInfo implements Converter<FileStorageMetadata, FileInfo>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public FileInfo convert(FileStorageMetadata t) {
        FileInfo fileInfo = new FileInfo();

        fileInfo.setFileName(t.getFileName());
        fileInfo.setFileExtension(FileUtils.getExtension(t.getFileName()));
        fileInfo.setStorageType(t.getStorageType());

        fileInfo.setCreateTime(Calendar.getInstance());
        fileInfo.setCreateUserId(UserContext.getUserId());
        fileInfo.setCreateUserName(UserContext.getUsername());

        fileInfo.setProperties(Json.toJson(t));
        return fileInfo;
    }

}
