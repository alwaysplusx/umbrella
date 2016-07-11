package com.huiju.module.fs.eao;

import javax.ejb.Stateless;

import com.huiju.module.fs.entity.FileInfo;

/**
 * 上传文件EaoBean
 * 
 * @author chenyx
 */
@Stateless(mappedName = "FileInfoEaoBean")
public class FileInfoEaoBean extends GenericCoreEao<FileInfo, Long> implements FileInfoEaoLocal {

}
