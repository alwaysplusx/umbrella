package com.huiju.module.fs.eao;

import javax.ejb.Local;

import com.huiju.module.data.eao.GenericEao;
import com.huiju.module.fs.entity.FileGroup;

/**
 * 上传文件组EaoLocal
 * 
 * @author chenyx
 */
@Local
public interface FileGroupEaoLocal extends GenericEao<FileGroup, Long> {

}
