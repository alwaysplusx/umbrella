package com.huiju.module.fs.eao;

import javax.ejb.Stateless;

import com.huiju.module.fs.entity.FileGroup;

/**
 * 上传文件组EaoBean
 * 
 * @author chenyx
 */
@Stateless(mappedName = "FileGroupEaoBean")
public class FileGroupEaoBean extends GenericCoreEao<FileGroup, Long> implements FileGroupEaoLocal {

}
