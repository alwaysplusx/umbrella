package com.harmony.umbrella.data.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.harmony.umbrella.data.Persistable;

/**
 * @author wuxii@foxmail.com
 */
@MappedSuperclass
public abstract class Model<ID extends Serializable> implements Persistable<ID> {

    private static final long serialVersionUID = -9098668260590791573L;

    @Column(updatable = false)
    protected Long creatorId;

    @Column(updatable = false)
    protected String creatorName;

    @Column(updatable = false)
    protected String creatorCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    protected Calendar createdTime;

    protected Long modifierId;

    protected String modifierName;

    protected String modifierCode;

    @Temporal(TemporalType.TIMESTAMP)
    protected Calendar modifiedTime;

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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorCode() {
        return creatorCode;
    }

    public void setCreatorCode(String creatorCode) {
        this.creatorCode = creatorCode;
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {
        this.createdTime = createdTime;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public String getModifierCode() {
        return modifierCode;
    }

    public void setModifierCode(String modifierCode) {
        this.modifierCode = modifierCode;
    }

    public Calendar getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Calendar modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "{\"" + getClass().getSimpleName() + "\":" + "{\"id\":\"" + getId() + "\"}}";
    }

}
