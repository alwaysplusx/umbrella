package com.harmony.umbrella.data.util;

import static com.harmony.umbrella.context.ContextHelper.*;
import java.util.Date;

import com.harmony.umbrella.data.domain.BaseEntity;

/**
 * @author wuxii@foxmail.com
 */
public class DataUtils {

    public static void applyUserInfoIfNecessary(BaseEntity<?> entity) {
        applyCreatorInfoIfNecessary(entity);
        applyModifierInfoIfNecessary(entity);
    }

    public static void applyCreatorInfoIfNecessary(BaseEntity<?> entity) {
        if (entity.getCreatedTime() == null) {
            entity.setCreatedTime(new Date());
        }
        if (entity.getCreatorId() == null) {
            entity.setCreatorId(getUserId());
        }
        if (entity.getCreatorCode() == null) {
            entity.setCreatorCode(getUsername());
        }
        if (entity.getCreatorName() == null) {
            entity.setCreatorName(getNickname());
        }
    }

    public static void applyModifierInfoIfNecessary(BaseEntity<?> entity) {
        if (entity.getModifiedTime() == null) {
            entity.setModifiedTime(new Date());
        }
        if (entity.getModifierId() == null) {
            entity.setModifierId(getUserId());
        }
        if (entity.getModifierCode() == null) {
            entity.setModifierCode(getUsername());
        }
        if (entity.getModifierName() == null) {
            entity.setModifierName(getNickname());
        }
    }

}
