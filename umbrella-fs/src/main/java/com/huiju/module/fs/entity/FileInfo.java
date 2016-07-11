package com.huiju.module.fs.entity;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.BaseEntity;
import com.huiju.module.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "S_FILEINFO")
public class FileInfo extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FileInfo_PK")
    @TableGenerator(name = "FileInfo_PK", table = "s_pkGenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "FileInfo_PK", allocationSize = 1)
    private Long fileInfoId;

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件的扩展名(小写)
     */
    private String fileExtension;

    private String properties;

    private String storageType;

    private String remark;

    private Long createUserId;
    private String createUserName;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTime;

    @ManyToOne
    @JoinColumn(name = "FILEGROUPID")
    private FileGroup fileGroup;

    public Long getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId(Long fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public StorageType getStorageType() {
        return storageType != null ? StorageType.valueOf(storageType) : null;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType != null ? storageType.name() : null;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }

    public FileGroup getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(FileGroup fileGroup) {
        this.fileGroup = fileGroup;
    }

}