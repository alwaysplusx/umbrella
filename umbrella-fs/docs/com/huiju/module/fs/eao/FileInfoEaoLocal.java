package com.huiju.module.fs.eao;

import javax.ejb.Local;

import com.huiju.module.data.eao.GenericEao;
import com.huiju.module.fs.entity.FileInfo;

/**
 * 上传文件EaoLocal
 * 
 * @author chenyx
 */
@Local
public interface FileInfoEaoLocal extends GenericEao<FileInfo, Long> {

}
