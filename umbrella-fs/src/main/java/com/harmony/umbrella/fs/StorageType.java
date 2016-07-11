package com.harmony.umbrella.fs;

/**
 * @author wuxii@foxmail.com
 */
public enum StorageType {

    AWS("amazon s3"), SERVER("server file system"), DB("database"), FTP("ftp");

    private String desc;

    private StorageType(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return desc;
    }

}
