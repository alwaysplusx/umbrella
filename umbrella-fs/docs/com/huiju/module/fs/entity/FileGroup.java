package com.huiju.module.fs.entity;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.BaseEntity;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "S_FILEGROUP")
public class FileGroup extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FileGroup_PK")
    @TableGenerator(name = "FileGroup_PK", table = "s_pkGenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "FileGroup_PK", allocationSize = 1)
    private Long fileGroupId;

    private String remark;

    private Long createUserId;
    private String createUserName;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTime;

    @OneToMany(mappedBy = "fileGroup")
    private List<FileInfo> fileInfos;

    public Long getFileGroupId() {
        return fileGroupId;
    }

    public void setFileGroupId(Long fileGroupId) {
        this.fileGroupId = fileGroupId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

}
