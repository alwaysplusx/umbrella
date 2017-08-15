package com.harmony.umbrella.web.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.data.domain.BaseEntity;

/**
 * @author wuxii@foxmail.com
 */
public final class WebUtils extends ContextHelper {

    public static void applyUserInfoIfNecessary(BaseEntity<?> entity) {
        applyCreatorInfoIfNecessary(entity);
        applyModifierInfoIfNecessary(entity);
    }

    public static void applyCreatorInfoIfNecessary(BaseEntity<?> entity) {
        entity.setCreatedTime(new Date());
        entity.setCreatorCode(getUsername());
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getNickname());
    }

    public static void applyModifierInfoIfNecessary(BaseEntity<?> entity) {
        entity.setModifiedTime(new Date());
        entity.setModifierCode(getUsername());
        entity.setModifierId(getUserId());
        entity.setModifierName(getNickname());
    }

    public static boolean hasHttpRequest() {
        return getHttpRequest() != null;
    }

    public static HttpSession getHttpSession(boolean create) {
        HttpServletRequest request = getHttpRequest();
        return request == null ? null : request.getSession(create);
    }

}
