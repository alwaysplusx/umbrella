package com.harmony.umbrella.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

/**
 * @author wuxii@foxmail.com
 */
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Persistable<ID> {

    private static final long serialVersionUID = -9098668260590791573L;

    @Column(updatable = false)
    protected Long creatorId;

    @Column(updatable = false)
    protected String creatorCode;

    @Column(updatable = false)
    protected String creatorName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    protected Date createdTime;

    protected Long modifierId;

    protected String modifierCode;

    protected String modifierName;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedTime;

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorCode() {
        return creatorCode;
    }

    public void setCreatorCode(String creatorCode) {
        this.creatorCode = creatorCode;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierCode() {
        return modifierCode;
    }

    public void setModifierCode(String modifierCode) {
        this.modifierCode = modifierCode;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "{\"" + getClass().getSimpleName() + "\":" + "{\"id\":\"" + getId() + "\"}}";
    }

}
