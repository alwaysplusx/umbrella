package com.harmony.umbrella.data.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.data.util.DataUtils;

/**
 * @author wuxii@foxmail.com
 */
public class OperationInfoInjectEntityListener {

    @PrePersist
    public void applyCreatorInfo(BaseEntity<?> entity) {
        DataUtils.applyCreatorInfoIfNecessary(entity);
    }

    @PreUpdate
    public void applyModifierInfo(BaseEntity<?> entity) {
        DataUtils.applyModifierInfoIfNecessary(entity);
    }

}
