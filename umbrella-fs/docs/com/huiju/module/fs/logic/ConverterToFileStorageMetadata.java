package com.huiju.module.fs.logic;

import java.io.Serializable;

import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.entity.FileInfo;
import com.huiju.module.fs.support.DefaultFileStorageMetadata;
import com.huiju.module.json.Json;
import com.huiju.module.util.Converter;

/**
 * @author wuxii@foxmail.com
 */
public class ConverterToFileStorageMetadata implements Converter<FileInfo, FileStorageMetadata>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public FileStorageMetadata convert(FileInfo t) {
        String properties = t.getProperties();
        return Json.parse(properties, DefaultFileStorageMetadata.class);
    }

}
