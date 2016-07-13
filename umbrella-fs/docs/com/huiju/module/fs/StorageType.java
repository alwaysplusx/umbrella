package com.huiju.module.fs;

/**
 * 所有的存储类型
 * 
 * @author wuxii@foxmail.com
 */
public enum StorageType {

    /**
     * amazon simple storage service
     */
    AWS("amazon s3"), //
    /**
     * server file system storage
     */
    SERVER("server file system"), //
    /**
     * database storage
     */
    DB("database"), //
    /**
     * ftp file system storage
     */
    FTP("ftp");

    private String desc;

    private StorageType(String desc) {
        this.desc = desc;
    }

    /**
     * 存储描述
     * 
     * @return
     */
    public String getDescription() {
        return desc;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
